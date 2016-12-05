package com.soundcloud.followermaze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by malam on 12/5/16.
 */

class ClientThread extends MessageBusSocketImpl {

    private ConcurrentHashMap<Integer, PrintWriter> clientStream;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Boolean>> followerMap;

    public ClientThread(Socket socket, ConcurrentHashMap clientStream, ConcurrentHashMap followerMap) {
        mBus = socket;
        this.clientStream = clientStream;
        this.followerMap = followerMap;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            initBus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    void process() throws IOException {
        String input;
        try {
            while ((input = in.readLine()) != null) {
                System.out.println(input);
                clientStream.put(Integer.parseInt(input), out);
                followerMap.put(Integer.parseInt(input), new ConcurrentHashMap<>());
                System.out.println("Channel size : " + clientStream.keySet().size() + ", FollowerMap size : " + followerMap.keySet().size());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class ClientHandler extends Thread {

    private ConcurrentHashMap<Integer, PrintWriter> clientStream;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Boolean>> followerMap;
    private ServerSocket clientSource;

    public ClientHandler(int port, ConcurrentHashMap clientStream, ConcurrentHashMap followerMap) {
        this.clientStream = clientStream;
        this.followerMap = followerMap;
        try {
            clientSource = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                clientSource.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void run() {

        while (true) {
            try {
                new ClientThread(clientSource.accept(), clientStream, followerMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
