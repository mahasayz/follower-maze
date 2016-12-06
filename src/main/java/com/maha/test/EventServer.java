package com.maha.test;

import com.soundcloud.followermaze.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by malam on 12/6/16.
 */
public class EventServer extends Thread {

    public final static int PORT = 9090;
    private final static Logger logger = Logger.getLogger("events");
    public static ConcurrentSkipListMap<Integer, Event> eventMap;
    public static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Boolean>> followerMap;

    public EventServer() {
        eventMap = new ConcurrentSkipListMap<>();
        followerMap = new ConcurrentHashMap<>();
        logger.setLevel(Level.INFO);
    }

    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(48);

        try(ServerSocket server = new ServerSocket(PORT)) {
            logger.log(Level.INFO, "EventSource Handler started and listening for connections");
            while(true) {
                Socket connection = server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Callable<Void> task = new EventTask(in);
                pool.submit(task);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Event Handler : Couldn't start server", e);
        }
    }

    private static class EventTask implements Callable<Void> {

        private BufferedReader in;

        EventTask(BufferedReader in) {
            this.in = in;
        }

        public void parseEvent(String input) throws Exception {
            String[] tokens = input.split("\\|");
            switch (tokens.length) {
                case 4: {
                    eventMap.put(Integer.parseInt(tokens[0]), new Event(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), input));
                    break;
                }
                case 3: {
                    eventMap.put(Integer.parseInt(tokens[0]), new Event(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), -1, input));
                    break;
                }
                case 2: {
                    eventMap.put(Integer.parseInt(tokens[0]), new Event(Integer.parseInt(tokens[0]), tokens[1], -1, -1, input));
                    break;
                }
                default: throw new Exception("Unable to parse " + input);
            }
        }

        public Void call() {
            try {
                String input;
                while ((input = in.readLine()) != null) {
                    logger.log(Level.INFO, "Event Handler : " + input);
                    parseEvent(input);
                }
            } catch (IOException e) {
                System.err.println(e);
            } catch (Exception e) {
                System.err.println(e);
            }
            return null;
        }
    }

}
