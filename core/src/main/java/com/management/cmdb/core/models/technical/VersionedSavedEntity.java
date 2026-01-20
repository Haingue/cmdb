package com.management.cmdb.core.models.technical;

import com.management.cmdb.core.models.business.identity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
public class VersionedSavedEntity extends UniqueEntity {

    protected long revision;
    protected List<Event> events;
    protected LocalDateTime creationDatetime;
    protected LocalDateTime archiveDatetime;

    public static VersionedSavedEntity reload(UUID uuid, long version, List<Event> events, LocalDateTime creationDatetime, LocalDateTime archiveDatetime) {
        return new VersionedSavedEntity(
                uuid,
                version,
                Objects.requireNonNullElseGet(events, ArrayList::new),
                creationDatetime,
                archiveDatetime
        );
    }

    public VersionedSavedEntity() {
        super();
        this.revision = 1L;
        this.events = new ArrayList<>();
        this.creationDatetime = LocalDateTime.now();
    }

    // @Builder(buildMethodName = "reload")
    public VersionedSavedEntity(UUID uuid, long revision, List<Event> events, LocalDateTime creationDatetime, LocalDateTime archiveDatetime) {
        super(new UniqueEntity(uuid));
        assert revision > 0;
        this.revision = revision;
        this.events = events;
        this.creationDatetime = creationDatetime;
        this.archiveDatetime = archiveDatetime;
    }

    public VersionedSavedEntity(VersionedSavedEntity source) {
        this(source.getUuid(), source.revision, source.events, source.creationDatetime, source.archiveDatetime);
    }

    protected void increaseVersion() {
        this.revision++;
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

    public boolean isArchived () {
        return archiveDatetime != null;
    }

    public void archive(User user) {
        this.archive(user.uuid().toString(), LocalDateTime.now());
    }

    public void archive(String initiator) {
        this.archive(initiator, LocalDateTime.now());
    }

    public void archive(String initiator, LocalDateTime archiveDatetime) {
        this.archiveDatetime = archiveDatetime;
        this.update(new Event(EventType.ARCHIVE, null, initiator));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VersionedSavedEntity that)) return false;
        return super.equals(that) && revision == that.revision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), revision);
    }

    @Override
    public String toString() {
        return "VersionedSavedEntity{" +
                "uuid=" + super.getUuid() +
                ", version=" + revision +
                ", creationDatetime=" + creationDatetime +
                ", deletionDatetime=" + archiveDatetime +
                '}';
    }
}
