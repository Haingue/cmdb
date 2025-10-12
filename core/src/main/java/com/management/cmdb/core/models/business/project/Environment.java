package com.management.cartography.core.models.business.project;

import com.management.cartography.core.models.business.component.Component;
import com.management.cartography.core.models.business.constants.EnvironmentStatus;
import com.management.cartography.core.models.business.constants.EnvironmentType;
import com.management.cartography.core.models.technical.Entity;

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
        this.location = location;
        this.type = type;
        this.components = new HashSet<Component>();
        this.jiraTracker = jiraTracker;
        this.status = EnvironmentStatus.REQUESTED;
    }

    protected Environment(String location, EnvironmentType type, Set<Component> components, String jiraTracker, EnvironmentStatus status) {
        this.location = location;
        this.type = type;
        this.components = components;
        this.jiraTracker = jiraTracker;
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public EnvironmentType getType() {
        return type;
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

    public EnvironmentStatus getStatus() {
        return status;
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

}
