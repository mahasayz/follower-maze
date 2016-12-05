package com.soundcloud.followermaze;

import java.io.IOException;

/**
 * Created by malam on 12/5/16.
 */
public interface MessageBus {

    void initBus() throws IOException;
    String getMBusName();
    void addChannel(String name);
    void publishChannelMessage(String name, String message) throws IOException;

}
