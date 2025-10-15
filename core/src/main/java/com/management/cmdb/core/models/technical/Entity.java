package com.management.cmdb.core.models.technical;

import com.management.cmdb.core.models.business.identity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Entity {

    private UUID uuid;
    private long version;
    private List<Event> events;
    private LocalDateTime creationDatetime;
    private LocalDateTime deletionDatetime;

    protected Entity() {
        this.uuid = UUID.randomUUID();
        this.version = 1L;
        this.events = new ArrayList<>();
        this.creationDatetime = LocalDateTime.now();
    }

    protected Entity(UUID uuid, long version, List<Event> events, LocalDateTime creationDatetime, LocalDateTime deletionDatetime) {
        this.uuid = uuid;
        this.version = version;
        this.events = events;
        this.creationDatetime = creationDatetime;
        this.deletionDatetime = deletionDatetime;
    }

    protected void reloadEntity (Entity entity) {
        this.reloadEntity(entity.getUuid(), entity.getVersion(), entity.getEvents(), entity.getCreationDatetime(), entity.getDeletionDatetime());
    }

    protected void reloadEntity (UUID uuid, long version, List<Event> events, LocalDateTime creationDatetime, LocalDateTime deletionDatetime) {
        this.uuid = uuid;
        this.version = version;
        this.events = events;
        this.creationDatetime = creationDatetime;
        this.deletionDatetime = deletionDatetime;
    }

    public UUID getUuid() {
        return uuid;
    }

    protected void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getVersion() {
        return version;
    }

    protected void increaseVersion() {
        this.version++;
    }

    public List<Event> getEvents() {
        return events;
    }

    protected boolean addEvent(Event event) {
        return this.events.add(event);
    }

    public boolean update (User user) {
        return this.update(user.uuid().toString());
    }

    public boolean update (String initiator) {
        return this.update(new Event(EventType.UPDATE, null, initiator));
    }

    public boolean update (Event event) {
        this.increaseVersion();
        return this.events.add(event);
    }

    public LocalDateTime getCreationDatetime() {
        return creationDatetime;
    }

    public LocalDateTime getDeletionDatetime() {
        return deletionDatetime;
    }

    public boolean isDeleted () {
        return deletionDatetime != null;
    }

    public void delete (User user) {
        this.deletionDatetime = LocalDateTime.now();
        this.update(new Event(EventType.DELETE, null, user.uuid().toString()));
    }

    public void delete (String initiator) {
        this.deletionDatetime = LocalDateTime.now();
        this.update(new Event(EventType.DELETE, null, initiator));
    }

    @Override
    public String toString() {
        return "Entity{" +
                "uuid=" + uuid +
                ", version=" + version +
                ", creationDatetime=" + creationDatetime +
                ", deletionDatetime=" + deletionDatetime +
                '}';
    }
}
