package com.soundcloud.followermaze;

/**
 * Created by Mahbub on 12/4/2016.
 */
public class Event {

    private int id;
    private String type;
    private int fromId;
    private int toId;
    private String message;

    public Event(int id, String type, int fromId, int toId, String message) {
        this.id = id;
        this.type = type;
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
    }

    public int getId() { return this.id; }
    public String getType() { return this.type; }
    public int getFromId() { return this.fromId; }
    public int getToId() { return this.toId; }
    public String getMessage() { return this.message; }

}
