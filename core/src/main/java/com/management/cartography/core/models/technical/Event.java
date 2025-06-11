package com.management.cartography.core.models.technical;

import com.management.cartography.core.models.business.identity.User;

import java.time.Instant;
import java.util.Objects;

public class Event {

    private Instant timestamp;
    private EventType type;
    private String description;
    private String initiator;

    public Event(EventType type, String description, String initiator) {
        this.initiator = initiator;
        this.timestamp = Instant.now();
        this.type = type;
        this.description = description;
    }

    public Event(Instant timestamp, EventType type, String description, String initiator) {
        this.timestamp = timestamp;
        this.type = type;
        this.description = description;
        this.initiator = initiator;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event event)) return false;
        return Objects.equals(timestamp, event.timestamp) && type == event.type && Objects.equals(initiator, event.initiator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, type, initiator);
    }

    @Override
    public String toString() {
        return "Event{" +
                "timestamp=" + timestamp +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", initiator='" + initiator + '\'' +
                '}';
    }
}
