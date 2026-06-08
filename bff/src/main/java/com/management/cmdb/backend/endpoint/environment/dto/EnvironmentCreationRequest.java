package com.management.cmdb.backend.endpoint.environment.dto;

import com.management.cmdb.core.models.business.identity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentCreationRequest {
    private UUID uuid;
    private User requestor;
    private Instant requestTimestamp;
    private EnvironmentDto environment;
    private UUID projectUuid;

    @Override
    public String toString() {
        return "ProjectCreationRequest{" +
                "uuid=" + getUuid() +
                ", requestor=" + getRequestor() +
                ", projectUuid=" + projectUuid +
                ", environment=" + environment +
                '}';
    }
}
