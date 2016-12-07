package com.soundcloud.followermaze;

/**
 * The event data structure
 * @author Mahbub Alam
 */
public class Event {
    private int seqID;
    private EventType type;
    private int fromID;
    private int toID;
    private String message;

    /**
     *
     * @param seqID the sequence id of the event
     * @param type  the type of the event
     * @param fromID the user who initiated the event
     * @param toID  the target user affected by the event
     * @param message   the raw event message
     * @throws Exception throws an exception if the raw event is malformed
     */
    public Event(int seqID, String type, int fromID, int toID, String message) throws Exception {
        this.seqID = seqID;
        this.fromID = fromID;
        this.toID = toID;
        this.message = message;
        switch (type) {
            case "F": {
                this.type = EventType.FOLLOW;
                break;
            }
            case "B": {
                this.type = EventType.BROADCAST;
                break;
            }
            case "U": {
                this.type = EventType.UNFOLLOW;
                break;
            }
            case "P": {
                this.type = EventType.PRIVATE;
                break;
            }
            case "S": {
                this.type = EventType.STATUS;
                break;
            }
            default: throw new Exception("Event type not recognized - " + type);
        }
    }

    public int getSeqID() { return  this.seqID; }
    public EventType getType() { return  this.type; }
    public int getFromID() { return  this.fromID; }
    public int getToID() { return  this.toID; }
    public String getMessage() { return  this.message; }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;
        return this.seqID == event.getSeqID() &&
                this.type == event.getType() &&
                this.fromID == event.getFromID() &&
                this.toID == event.getToID();
    }
}