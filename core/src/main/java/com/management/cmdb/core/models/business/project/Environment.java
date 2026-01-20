package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.technical.Event;
import com.management.cmdb.core.models.technical.VersionedSavedEntity;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class Environment extends VersionedSavedEntity {

    private String location;
    private EnvironmentType type;

    private Set<Component> components;

    private String jiraTracker;
    private EnvironmentStatus status;

    public Environment(String location, EnvironmentType type, String jiraTracker) {
        super();
        this.location = location;
        this.type = type;
        this.components = new HashSet<>();
        this.jiraTracker = jiraTracker;
        this.status = EnvironmentStatus.REQUESTED;
    }

    public Environment(@NonNull UUID uuid, long revision, List<Event> events, LocalDateTime creationDatetime, LocalDateTime archiveDatetime,
                        String location, EnvironmentType type, Set<Component> components, String jiraTracker, EnvironmentStatus status) {
        super(VersionedSavedEntity.reload(uuid, revision, events, creationDatetime, archiveDatetime));
        this.location = location;
        this.type = type;
        this.components = Objects.requireNonNullElseGet(components, HashSet::new);
        this.jiraTracker = jiraTracker;
        this.status = status;
    }

    public Environment (Environment source) {
        this(source.getUuid(), source.revision, source.events, source.creationDatetime, source.archiveDatetime, source.location, source.type, source.components, source.jiraTracker, source.status);
    }

    public void addComponents (Component component) {
        this.components.add(component);
    }

    public Set<Component> getOutDatedComponents() {
        return components.stream()
                .filter(Component::needsUpdate).collect(Collectors.toUnmodifiableSet());
    }

    public void checkIntegrity() {
        if (StringUtils.isBlank(location)) throw new InvalidObjectException("location cannot be blank", this);
        if (type == null) throw new InvalidObjectException("type cannot be null", this);
        if (status == null) throw new InvalidObjectException("status cannot be null", this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Environment that)) return false;
        return super.equals(that) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, type);
    }

    @Override
    public String toString() {
        return "Environment{" +
                "uuid=" + getUuid() +
                ", version=" + revision +
                ", location='" + location + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", version=" + revision +
                '}';
    }
}
