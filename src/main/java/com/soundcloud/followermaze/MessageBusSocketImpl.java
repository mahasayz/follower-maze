package com.soundcloud.followermaze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by malam on 12/5/16.
 */
public abstract class MessageBusSocketImpl implements MessageBus, Runnable {

    protected String mBusName;
    protected Socket mBus;
    protected Thread processor;
    protected BufferedReader in;
    protected PrintWriter out;

    public MessageBusSocketImpl(String name, Socket mBus) {
        this.mBusName = name;
        this.mBus = mBus;
    }

    public MessageBusSocketImpl() {

    }

    @Override
    public void initBus() throws IOException {
        processor = new Thread(this);
        processor.start();

    }

    @Override
    public String getMBusName() {
        return null;
    }

    @Override
    public void addChannel(String name) {

    }

    @Override
    public void publishChannelMessage(String name, String message) throws IOException {

    }

    abstract void process() throws IOException;

    public void run() {
        while (true) {
            try {
                process();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mBus.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
