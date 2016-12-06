package com.maha.test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by malam on 12/6/16.
 */
public class ClientServer extends Thread {

    public final static int PORT = 9099;
    private final static Logger logger = Logger.getLogger("clients");
    public static ConcurrentHashMap<Integer, PrintWriter> clientStream;

    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(24);
        clientStream = new ConcurrentHashMap<>();

        try(ServerSocket server = new ServerSocket(PORT)) {
            logger.log(Level.INFO, "Client Handler started and listening for connections");
            while(true) {
                try {
                    Socket connection = server.accept();
                    Callable<Void> task = new ClientTask(connection);
                    pool.submit(task);
                } catch (IOException e) {}
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Client Handler : Couldn't start server", e);
        }
    }

    private static class ClientTask implements Callable<Void> {

        private Socket connection;

        ClientTask(Socket connection) {
            this.connection = connection;
        }

        public Void call() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                int clientID = Integer.parseInt(in.readLine());
                logger.log(Level.INFO, "Client Handler : Client says " + clientID);
                clientStream.put(clientID, new PrintWriter(connection.getOutputStream()));
            } catch (IOException e) {
                System.err.println(e);
            }
            return null;
        }
    }
}
