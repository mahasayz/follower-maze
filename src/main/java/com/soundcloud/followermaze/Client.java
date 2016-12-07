package com.soundcloud.followermaze;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The client data structure to store client connections
 * @author Mahbub Alam
 */
public class Client {

    private int clientID;
    private PrintWriter out;
    private final BlockingQueue<String> input;

    /**
     *
     * @param clientID  the client id
     * @param os    the output stream for communicating with client
     */
    public Client(int clientID, OutputStream os) {
        this.clientID = clientID;
        this.out = new PrintWriter(os);
        this.input = new LinkedBlockingDeque<>();
    }

    public int getClientID() { return clientID; }

    public PrintWriter getOut() { return this.out; }

}
