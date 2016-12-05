package com.soundcloud.followermaze;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by malam on 12/5/16.
 */
class Workers extends Thread {

    private ConcurrentSkipListMap<Integer, Event> eventMap;
    private ConcurrentHashMap<Integer, PrintWriter> clientStream;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Boolean>> followerMap;
    private AtomicInteger counter;

    public Workers(ConcurrentSkipListMap eventMap, ConcurrentHashMap clientStream, ConcurrentHashMap followerMap, AtomicInteger counter) {
        this.eventMap = eventMap;
        this.clientStream = clientStream;
        this.followerMap = followerMap;
        this.counter = counter;
    }

    public void run() {
        while(true) {
            while (eventMap.keySet().size() > 0) {
                Event event = eventMap.firstEntry().getValue();
                if (event.getId() != counter.get())
                    continue;
                System.out.println("Handling event with Id " + event.getId());
                switch (event.getType()) {
                    case "F": {
                        if (event.getFromId() == event.getToId())
                            System.out.println("From and To are similar for " + event.getId());
                        PrintWriter socket = clientStream.getOrDefault(event.getToId(), null);
                        ConcurrentHashMap<Integer, Boolean> list = followerMap.get(event.getToId());
                        if (list == null) {
                            list = new ConcurrentHashMap();
                        }
                        list.put(event.getFromId(), true);
                        followerMap.put(event.getToId(), list);
                        if (socket != null) {
                            socket.println(event.getMessage());
                        }
                        eventMap.remove(event.getId());
                        System.out.println("Counter : " + counter.incrementAndGet());
                        break;
                    }
                    case "U": {
                        eventMap.remove(event.getId());
                        ConcurrentHashMap<Integer, Boolean> list = followerMap.get(event.getToId());
                        if (list != null) {
                            if (list.getOrDefault(event.getFromId(), false))
                                list.remove(event.getFromId());
                        }
                        System.out.println("Counter : " + counter.incrementAndGet());
                        break;
                    }
                    case "B": {
                        for (PrintWriter socket : clientStream.values()) {
                            socket.println(event.getMessage());
                        }
                        eventMap.remove(event.getId());
                        System.out.println("Counter : " + counter.incrementAndGet());
                        break;
                    }
                    case "P": {
                        PrintWriter socket = clientStream.getOrDefault(event.getToId(), null);
                        if (socket != null) {
                            socket.println(event.getMessage());
                        }
                        eventMap.remove(event.getId());
                        System.out.println("Counter : " + counter.incrementAndGet());
                        break;
                    }
                    case "S": {
                        ConcurrentHashMap<Integer, Boolean> map = followerMap.get(event.getFromId());
                        if (map != null) {
                            for (Integer toId : map.keySet()) {
                                PrintWriter socket = clientStream.getOrDefault(toId, null);
                                if (socket == null) {
                                    System.out.println("Couldn't get " + toId + " from channelMap");
                                    continue;
                                }
                                socket.println(event.getMessage());
                            }
                        }

                        System.out.println("Counter : " + counter.incrementAndGet());
                        eventMap.remove(event.getId());
                        break;
                    }
                    default:
                        System.out.print("Got a weird thing");
                }
            }
        }
    }
}

public class Main {

    private ConcurrentSkipListMap<Integer, Event> eventMap;
    private ConcurrentHashMap<Integer, PrintWriter> clientStream;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Boolean>> followerMap;
    private ExecutorService eventHandlerPool = Executors.newFixedThreadPool(32);
    private ExecutorService clientHandlerPool = Executors.newFixedThreadPool(16);
    private AtomicInteger counter;

    public void init() throws InterruptedException, IOException {
        eventMap = new ConcurrentSkipListMap<>();
        clientStream = new ConcurrentHashMap<>();
        followerMap = new ConcurrentHashMap<>();
        EventSourceHandler eventSourceHandler = new EventSourceHandler(9090, eventMap);
        eventSourceHandler.initBus();
        clientHandlerPool.execute(new ClientHandler(9099, clientStream, followerMap));
        counter = new AtomicInteger(1);
        eventHandlerPool.execute(new Workers(eventMap, clientStream, followerMap, counter));
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Main main = new Main();
        main.init();
    }

}
