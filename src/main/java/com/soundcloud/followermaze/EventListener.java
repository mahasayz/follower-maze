package com.soundcloud.followermaze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by malam on 12/7/16.
 */
public class EventListener implements Runnable {

    private BufferedReader in;
    private static ConcurrentSkipListMap<Integer, Event> eventMap;

    public EventListener(InputStream in, ConcurrentSkipListMap e) {
        this.in = new BufferedReader(new InputStreamReader(in));
        eventMap = e;
    }

    public Event parseInput(String input) {
        String[] tokens = input.split("\\|");
        if (tokens.length == 4) {
            return new Event(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), input);
        } else if (tokens.length == 3)
            return new Event(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), -1, input);
        else
            return new Event(Integer.parseInt(tokens[0]), tokens[1], -1, -1, input);
    }

    public void run() {
        String input;
        try {
            while ((input = in.readLine()) != null) {
                System.out.println("Got input " + input);
                Event event = parseInput(input);
                eventMap.put(event.getSeqID(), event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
