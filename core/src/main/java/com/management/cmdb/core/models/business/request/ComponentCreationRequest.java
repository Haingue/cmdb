package com.management.cmdb.core.models.business.request;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.technical.ComponentVisitor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class ComponentCreationRequest extends Request {

    private final UUID projectUuid;
    private final UUID environmentUuid;
    private final Component component;

    public ComponentCreationRequest(UUID uuid, User requestor, Instant requestTimestamp, UUID projectUuid, UUID environmentUuid, Component component) {
        super(uuid, requestor, requestTimestamp);
        this.projectUuid = projectUuid;
        this.environmentUuid = environmentUuid;
        this.component = component;
    }

    public ComponentCreationRequest(UUID projectUuid, UUID environmentUuid, Component component) {
        this.projectUuid = projectUuid;
        this.environmentUuid = environmentUuid;
        this.component = component;
    }

    public abstract <T> T accept(ComponentVisitor<T> visitor);

    @Override
    public String toString() {
        return "ComponentCreationRequest{" +
                "uuid=" + getUuid() +
                ", requestor=" + getRequestor() +
                ", projectUuid=" + projectUuid +
                ", environmentUuid=" + environmentUuid +
                '}';
    }
}
