package com.soundcloud.followermaze;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by malam on 12/7/16.
 */
public class Client {

    private int clientID;
    private PrintWriter out;
    private final BlockingQueue<String> input;

    public Client(int clientID, OutputStream os) {
        this.clientID = clientID;
        this.out = new PrintWriter(os);
        this.input = new LinkedBlockingDeque<>();
    }

    public int getClientID() { return clientID; }

    public PrintWriter getOut() { return this.out; }

    /*public void consume(String message) {
        out.println(message);
    }

    public void run() {
        while (true) {
            try {
                consume(input.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/

}
