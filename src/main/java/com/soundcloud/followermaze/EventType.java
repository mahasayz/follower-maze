package com.soundcloud.followermaze;

public enum EventType {
	FOLLOW("F"),
	UNFOLLOW("U"),
	BROADCAST("B"),
	PRIVATE("P"),
	STATUS("S");

	private final String type;

	EventType(String eventType) {
		this.type = eventType;
	}

	public String getEventType() {
		return type;
	}
}
