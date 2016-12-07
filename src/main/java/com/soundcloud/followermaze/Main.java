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
 * Created by malam on 12/7/16.
 */
public class Main {

    private static ConcurrentSkipListMap<Integer, Event> eventMap;
    private static ConcurrentHashMap<Integer, Client> clients;

    public static void main(String[] args) throws IOException {

        ExecutorService pool = Executors.newCachedThreadPool();

        ServerSocket clientServer = new ServerSocket(9099);
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

        ServerSocket eventServer = new ServerSocket(9090);
        Socket eventSocket = eventServer.accept();
        pool.submit(new EventListener(eventSocket.getInputStream(), eventMap));

        new EventHandler(eventMap, clients).start();
    }

}
