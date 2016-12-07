package com.soundcloud.followermaze;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by Mahbub on 12/8/2016.
 */
public class EventHandlerSpec {

    private static ConcurrentSkipListMap<Integer, Event> eventMap;

    @BeforeClass
    public static void initialize() {
        eventMap = new ConcurrentSkipListMap<>();
    }

    @Test
    public void eventHandlerCheck() throws Exception {
        eventMap.put(4, new Event(4, "B", -1, -1, "4|B"));
        eventMap.put(2, new Event(2, "F", 100, 300, "2|F|100|300"));
        eventMap.put(1, new Event(1, "F", 200, 100, "1|F|200|100"));
        eventMap.put(3, new Event(3, "S", 300, -1, "3|S|300"));

        ConcurrentHashMap<Integer, Client> clients = new ConcurrentHashMap<>();
        OutputStream os = new ByteArrayOutputStream();
        clients.put(100, new Client(100, os));

        new EventHandler(eventMap, clients).start();

        Thread.sleep(5000);

        StringBuilder sb = new StringBuilder();
        sb.append("1|F|200|100")
                .append(System.lineSeparator())
                .append("3|S|300")
                .append(System.lineSeparator())
                .append("4|B")
                .append(System.lineSeparator());

        Assert.assertEquals(os.toString(), sb.toString());
    }

    @AfterClass
    public static void shutdown(){
        eventMap = null;
    }

}
