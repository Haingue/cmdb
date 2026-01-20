package com.management.cmdb.core.models.business.request;

import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.Environment;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class EnvironmentCreationRequest extends Request {
    private final UUID projectId;
    private final List<Environment> environment;

    public EnvironmentCreationRequest(UUID uuid, User requestor, Instant requestTimestamp, UUID projectId, List<Environment> environment) {
        super(uuid, requestor, requestTimestamp);
        this.projectId = projectId;
        this.environment = environment;
    }

    @Override
    public String toString() {
        return "EnvironmentCreationRequest{" +
                "uuid=" + getUuid() +
                ", requestor=" + getRequestor() +
                ", projectId=" + projectId +
                '}';
    }
}
