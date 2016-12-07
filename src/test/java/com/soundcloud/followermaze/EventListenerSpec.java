package com.soundcloud.followermaze;

import org.junit.*;

import java.io.ByteArrayInputStream;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by Mahbub on 12/8/2016.
 */
public class EventListenerSpec {

    private static ConcurrentSkipListMap<Integer, Event> eventMap;

    @BeforeClass
    public static void initialize(){
        eventMap = new ConcurrentSkipListMap<>();
    }

    @Test
    public void eventMapCheck() throws Exception {
        String input = "666|F|60|50\r\n1|U|12|9\r\n542532|B\r\n43|P|32|56\r\n634|S|32";
        new Thread(new EventListener(new ByteArrayInputStream(input.getBytes("UTF-8")), eventMap)).start();

        // Wait for data to populate
        Thread.sleep(5000);

        TreeMap<Integer, Event> expected = new TreeMap<>();
        expected.put(666, new Event(666, "F", 60, 50, "666|F|60|50"));
        expected.put(1, new Event(1, "U", 12, 9, "1|U|12|9"));
        expected.put(542532, new Event(542532, "B", -1, -1, "542532|B"));
        expected.put(43, new Event(43, "P", 32, 56, "43|P|32|56"));
        expected.put(634, new Event(634, "S", 32, -1, "634|S|32"));

        Assert.assertEquals(5, eventMap.size());
        while (expected.size() > 0) {
            Assert.assertEquals(eventMap.pollFirstEntry(), expected.pollFirstEntry());
        }
    }

    @Test
    public void invalidFormatEventMapCheck() throws Exception {
        String input = "666|Y|A|50\r\n1|U|12|9\r\nB|B\r\nTest\r\n634|S|32";
        new Thread(new EventListener(new ByteArrayInputStream(input.getBytes("UTF-8")), eventMap)).start();

        // Wait for data to populate
        Thread.sleep(5000);

        TreeMap<Integer, Event> expected = new TreeMap<>();
        expected.put(1, new Event(1, "U", 12, 9, "1|U|12|9"));
        expected.put(634, new Event(634, "S", 32, -1, "634|S|32"));

        Assert.assertEquals(2, eventMap.size());
        while (expected.size() > 0) {
            Assert.assertEquals(eventMap.pollFirstEntry(), expected.pollFirstEntry());
        }
    }

    @AfterClass
    public static void shutdown(){
        System.out.println("Shutting down now");
    }

}
