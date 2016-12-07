package com.soundcloud.followermaze;

public enum EventType {
	/**
	 * A user wishes to follow the target user
     */
	FOLLOW("F"),
	/**
	 * A user wishes to unfollow the target user
     */
	UNFOLLOW("U"),
	/**
	 * A broadcast message meant for all connected users
     */
	BROADCAST("B"),
	/**
	 * A private message between two users
     */
	PRIVATE("P"),
	/**
	 * A status update meant for only followers of the user
     */
	STATUS("S");

	private final String type;

	EventType(String eventType) {
		this.type = eventType;
	}

	public String getEventType() {
		return type;
	}
}
