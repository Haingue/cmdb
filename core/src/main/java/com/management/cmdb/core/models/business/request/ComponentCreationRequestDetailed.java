package com.management.cmdb.core.models.business.request;

import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.models.technical.ComponentVisitor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// TODO check if a simpler object is better than embedded object ?
@Getter
public abstract class ComponentCreationRequestDetailed extends Request {

    private final UUID projectUuid;
    private final UUID environmentUuid;
    private final String name;
    private final String description;
    private final ComponentType type;
    private final Version version;
    private final String certificate;
    private final String technologyName;

    public ComponentCreationRequestDetailed(UUID uuid, User requestor, Instant requestTimestamp, UUID projectUuid, UUID environmentUuid, String name, String description, ComponentType type, Version version, String certificate, String technologyName) {
        super(uuid, requestor, requestTimestamp);
        this.projectUuid = projectUuid;
        this.environmentUuid = environmentUuid;
        this.name = name;
        this.description = description;
        this.type = type;
        this.version = version;
        this.certificate = certificate;
        this.technologyName = technologyName;
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
