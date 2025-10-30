package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.constants.EnvironmentStatus;
import com.management.cmdb.core.models.business.constants.EnvironmentType;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.technical.Entity;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Environment extends Entity {

    private String location;
    private EnvironmentType type;

    private Set<Component> components;

    private String jiraTracker;
    private EnvironmentStatus status;

    public static Environment create (String location, EnvironmentType type, String jiraTracker) {
        return new Environment(location, type, jiraTracker);
    }

    public static Environment load (Entity entity, String location, EnvironmentType type, String jiraTracker) {
        Environment environment = new Environment(location, type, jiraTracker);
        environment.reloadEntity(entity);
        return environment;
    }

    protected Environment(String location, EnvironmentType type, String jiraTracker) {
        super();
        this.location = location;
        this.type = type;
        this.components = new HashSet<Component>();
        this.jiraTracker = jiraTracker;
        this.status = EnvironmentStatus.REQUESTED;
    }

    protected Environment(String location, EnvironmentType type, Set<Component> components, String jiraTracker, EnvironmentStatus status) {
        super();
        this.location = location;
        this.type = type;
        this.components = components;
        this.jiraTracker = jiraTracker;
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public EnvironmentType getType() {
        return type;
    }

    public void setType(EnvironmentType type) {
        this.type = type;
    }

    public Set<Component> getComponents() {
        return Set.copyOf(components);
    }

    public boolean addComponents(Component component) {
        return components.add(component);
    }

    public String getJiraTracker() {
        return jiraTracker;
    }

    public void setJiraTracker(String jiraTracker) {
        this.jiraTracker = jiraTracker;
    }

    public EnvironmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnvironmentStatus status) {
        this.status = status;
    }

    public Set<Component> getOutDatedComponents() {
        return components.stream()
                .filter(Component::needsUpdate).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Environment that)) return false;
        return Objects.equals(location, that.location) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, type);
    }

    public boolean isValid () {
        if (StringUtils.isBlank(location)) throw new InvalidObjectException("location cannot be blank", this);
        if (type == null) throw new InvalidObjectException("type cannot be null", this);
        if (status == null) throw new InvalidObjectException("status cannot be null", this);
        return true;
    }
}
