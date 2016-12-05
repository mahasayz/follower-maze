package com.soundcloud.followermaze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by malam on 12/5/16.
 */
public class EventSourceHandler extends MessageBusSocketImpl {

    private ConcurrentSkipListMap<Integer, Event> eventMap;
    private ServerSocket eventSource;

    public EventSourceHandler(int port, ConcurrentSkipListMap eventMap) {
        this.eventMap = eventMap;
        try  {
            eventSource = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                eventSource.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    @Override
    void process() throws IOException {
        mBus = eventSource.accept();
        in = new BufferedReader(new InputStreamReader(mBus.getInputStream()));

        String input;
        while ((input = in.readLine()) != null) {
            String[] str = input.split("\\|");
            if (str.length == 4) {
                eventMap.put(Integer.parseInt(str[0]), new Event(Integer.parseInt(str[0]),
                        str[1], Integer.parseInt(str[2]), Integer.parseInt(str[3]), input));
            } else if (str.length == 3) {
                eventMap.put(Integer.parseInt(str[0]), new Event(Integer.parseInt(str[0]),
                        str[1], Integer.parseInt(str[2]), -1, input));
            } else {
                eventMap.put(Integer.parseInt(str[0]), new Event(Integer.parseInt(str[0]),
                        str[1], -1, -1, input));
            }
        }
    }
}
