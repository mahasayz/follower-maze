package com.maha.test;

import com.soundcloud.followermaze.Event;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.maha.test.EventServer.eventMap;
import static com.maha.test.EventServer.followerMap;

/**
 * Created by malam on 12/6/16.
 */
public class Main {

    private static EventServer eventServer;
    private static ClientServer clientServer;
    public static AtomicInteger counter = new AtomicInteger(1);
    private final static Logger logger = Logger.getLogger("processor");

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(16);
        clientServer = new ClientServer();
        eventServer = new EventServer();
        clientServer.start();
        eventServer.start();

        Worker[] workers = new Worker[16];
        for (int i=0; i<workers.length; i++) {
            workers[i] = new Worker();
            pool.execute(workers[i]);
        }

    }

    private static class Worker extends Thread {

        private void process(Event event) {
            logger.log(Level.INFO, "Processor: Came to process - " + event.getMessage());
            switch (event.getType()) {
                case "F": {
                    PrintWriter socket = clientServer.clientStream.getOrDefault(event.getToId(), null);
                    ConcurrentHashMap<Integer, Boolean> followers = followerMap.get(event.getToId());
                    if (followers == null) {
                        followers = new ConcurrentHashMap();
                    }
                    followers.put(event.getFromId(), true);
                    followerMap.put(event.getToId(), followers);
                    if (socket != null) {
                        socket.println(event.getMessage());
                        logger.log(Level.INFO, "Processor: Printed to channel - " + socket.toString());
                    }
                    eventMap.remove(event.getId());
                    logger.log(Level.INFO, "Processor: Counter = " + counter.incrementAndGet());
                    break;
                }
                case "U": {
                    eventMap.remove(event.getId());
                    ConcurrentHashMap<Integer, Boolean> followers = followerMap.get(event.getToId());
                    if (followers != null) {
                        if (followers.getOrDefault(event.getFromId(), false))
                            followers.remove(event.getFromId());
                    }
                    logger.log(Level.INFO, "Processor: Counter = " + counter.incrementAndGet());
                    break;
                }
                case "B": {
                    for (PrintWriter socket : clientServer.clientStream.values()) {
                        socket.println(event.getMessage());
                        logger.log(Level.INFO, "Processor: Printed to channel - " + socket.toString());
                    }
                    eventMap.remove(event.getId());
                    logger.log(Level.INFO, "Processor: Counter = " + counter.incrementAndGet());
                    break;
                }
                case "P": {
                    PrintWriter socket = clientServer.clientStream.getOrDefault(event.getToId(), null);
                    if (socket != null) {
                        socket.println(event.getMessage());
                        logger.log(Level.INFO, "Processor: Printed to channel - " + socket.toString());
                    }
                    eventMap.remove(event.getId());
                    logger.log(Level.INFO, "Processor: Counter = " + counter.incrementAndGet());
                    break;
                }
                case "S": {
                    ConcurrentHashMap<Integer, Boolean> map = followerMap.get(event.getFromId());
                    if (map != null) {
                        for (Integer toId : map.keySet()) {
                            PrintWriter socket = clientServer.clientStream.getOrDefault(toId, null);
                            if (socket == null) {
                                logger.log(Level.WARNING, "Couldn't get " + toId + " from channelMap");
                                continue;
                            }
                            socket.println(event.getMessage());
                            logger.log(Level.INFO, "Processor: Printed to channel - " + socket.toString());
                        }
                    }

                    logger.log(Level.INFO, "Processor: Counter = " + counter.incrementAndGet());
                    eventMap.remove(event.getId());
                    break;
                }
                default:
                    logger.log(Level.SEVERE, "Got a weird thing");
            }
        }

        public void run() {
            while(true) {
                while(eventMap.size() > 0) {
                    Map.Entry<Integer, Event> event = eventMap.firstEntry();
                    process(event.getValue());
                }
            }
        }
    }

}
