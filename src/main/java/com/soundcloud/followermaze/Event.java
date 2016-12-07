package com.soundcloud.followermaze;

import static com.soundcloud.EventType.*;

/**
 * Created by malam on 12/7/16.
 */
public class Event {
    private int seqID;
    private EventType type;
    private int fromID;
    private int toID;
    private String message;

    public Event(int seqID, String type, int fromID, int toID, String message) {
        this.seqID = seqID;
        this.fromID = fromID;
        this.toID = toID;
        this.message = message;
        switch (type) {
            case "F": {
                this.type = FOLLOW;
                break;
            }
            case "B": {
                this.type = BROADCAST;
                break;
            }
            case "U": {
                this.type = UNFOLLOW;
                break;
            }
            case "P": {
                this.type = PRIVATE;
                break;
            }
            case "S": {
                this.type = STATUS;
                break;
            }
        }
    }

    public int getSeqID() { return  this.seqID; }
    public EventType getType() { return  this.type; }
    public int getFromID() { return  this.fromID; }
    public int getToID() { return  this.toID; }
    public String getMessage() { return  this.message; }
}