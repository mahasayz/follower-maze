package com.soundcloud.followermaze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 *     This is the driver class of our application. It starts two <code>ServerSocket</code>
 *     instances meant for the event source and client, respectively.
 *     It also starts the <code>EventHandler</code> for processing the incoming events
 *     and routing messages to the connected clients.
 * </p>
 * @author Mahbub Alam
 */
public class Main {

    /**
     *
     */
    private static ConcurrentSkipListMap<Integer, Event> eventMap;
    private static ConcurrentHashMap<Integer, Client> clients;

    public static void main(String[] args) throws IOException {
        if (args.length != 2)
            throw new IllegalArgumentException("Syntax: com.soundcloud.followermaze.Main <event_source_port> <client_port>");

        int eventSourcePort = Integer.parseInt(args[0]);
        int clientPort = Integer.parseInt(args[1]);

        ExecutorService pool = Executors.newCachedThreadPool();

        ServerSocket clientServer = new ServerSocket(clientPort);
        clients = new ConcurrentHashMap<>();
        eventMap = new ConcurrentSkipListMap<>();

        pool.submit(() -> {
           while(true) {
               Socket clientSock = clientServer.accept();
               BufferedReader in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
               int clientID = Integer.parseInt(in.readLine());
               Client client = new Client(clientID, clientSock.getOutputStream());
               clients.put(clientID, client);
           }
        });

        ServerSocket eventServer = new ServerSocket(eventSourcePort);
        Socket eventSocket = eventServer.accept();
        pool.submit(new EventListener(eventSocket.getInputStream(), eventMap));

        new EventHandler(eventMap, clients).start();
    }

}
