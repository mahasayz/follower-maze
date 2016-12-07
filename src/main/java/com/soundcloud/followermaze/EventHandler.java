package com.soundcloud.followermaze;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *     This class mainly constitutes the event-handler thread which reads the events
 *     in a orderly manner and handles them according to their type.
 * </p>
 * @author Mahbub Alam
 */
public class EventHandler extends Thread {

    private static ConcurrentHashMap<Integer, Client> clients;
    private static ConcurrentSkipListMap<Integer, Event> eventMap;
    private static AtomicInteger counter = new AtomicInteger(1);
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Boolean>> followerMap;

    /**
     *
     * @param e the ordered map of events mapped according to their sequence ID
     * @param c the client map mapped according to client ID containing references to their output stream
     */
    public EventHandler(ConcurrentSkipListMap e, ConcurrentHashMap c) {
        clients = c;
        eventMap = e;
        followerMap = new ConcurrentHashMap<>();
    }

    /**
     * Contains the logic for processing each event as it happens
     * @param event the event to be processed
     */
    private void process(Event event) {
        switch (event.getType()) {
            case FOLLOW: {
                ConcurrentHashMap<Integer, Boolean> followers = followerMap.get(event.getToID());
                if (followers == null) {
                    followers = new ConcurrentHashMap();
                }
                followers.put(event.getFromID(), true);
                followerMap.put(event.getToID(), followers);
                if (clients.getOrDefault(event.getToID(), null) != null) {
                    PrintWriter out = clients.get(event.getToID()).getOut();
                    if (out != null) {
                        out.println(event.getMessage());
                        out.flush();
                    }
                }
                eventMap.remove(event.getSeqID());
                System.out.println("Came to process" + event.getMessage() + ", Counter " + counter.getAndIncrement());
                break;
            }
            case UNFOLLOW: {
                eventMap.remove(event.getSeqID());
                ConcurrentHashMap<Integer, Boolean> followers = followerMap.get(event.getToID());
                if (followers != null) {
                    if (followers.getOrDefault(event.getFromID(), false))
                        followers.remove(event.getFromID());
                }
                System.out.println("Came to process" + event.getMessage() + ", Counter " + counter.getAndIncrement());
                break;
            }
            case BROADCAST: {
                for (Client client : clients.values()) {
                    if (client.getOut() != null) {
                        client.getOut().println(event.getMessage());
                        client.getOut().flush();
                    }
                }
                eventMap.remove(event.getSeqID());
                System.out.println("Came to process" + event.getMessage() + ", Counter " + counter.getAndIncrement());
                break;
            }
            case PRIVATE: {
                if (clients.getOrDefault(event.getToID(), null) != null) {
                    PrintWriter out = clients.get(event.getToID()).getOut();
                    if (out != null) {
                        out.println(event.getMessage());
                        out.flush();
                    }
                }
                eventMap.remove(event.getSeqID());
                System.out.println("Came to process" + event.getMessage() + ", Counter " + counter.getAndIncrement());
                break;
            }
            case STATUS: {
                ConcurrentHashMap<Integer, Boolean> map = followerMap.get(event.getFromID());
                if (map != null && map.keySet().size() > 0) {
                    for (Integer toId : map.keySet()) {
                        if (clients.getOrDefault(toId, null) != null) {
                            PrintWriter socket = clients.get(toId).getOut();
                            if (socket == null) {
                                continue;
                            }
                            socket.println(event.getMessage());
                            socket.flush();
                        }
                    }
                }

                eventMap.remove(event.getSeqID());
                System.out.println("Came to process" + event.getMessage() + ", Counter " + counter.getAndIncrement());
                break;
            }
            default: // do nothing
        }
    }

    public void run() {
        while(true) {
            while (eventMap.size() > 0) {
                Map.Entry<Integer, Event> eventEntry = eventMap.firstEntry();
                if (eventEntry.getValue().getSeqID() != counter.get())
                    continue;
                process(eventEntry.getValue());
            }
        }
    }

}
