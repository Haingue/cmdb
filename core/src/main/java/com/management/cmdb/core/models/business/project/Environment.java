package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.technical.Checkable;
import com.management.cmdb.core.models.technical.VersionedSavedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Environment extends VersionedSavedEntity implements Checkable {

    private String name;
    private String description;
    private String location;
    private EnvironmentType type;

    @Builder.Default
    private Set<Component> components = new HashSet<>();

    private String jiraTracker;
    private EnvironmentStatus status;

    public void addComponents (Component component) {
        this.components.add(component);
    }

    public Set<Component> getOutDatedComponents() {
        return components.stream()
                .filter(Component::needsUpdate).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void checkIntegrity () throws InvalidObjectException {
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
