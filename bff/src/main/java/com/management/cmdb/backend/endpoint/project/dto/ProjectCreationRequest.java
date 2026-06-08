package com.management.cmdb.backend.endpoint.project.dto;

import com.management.cmdb.backend.endpoint.businessservice.dto.BusinessServiceDto;
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
public class ProjectCreationRequest {
    private UUID uuid;
    private User requestor;
    private Instant requestTimestamp;
    private ProjectDto project;
    private BusinessServiceDto businessService;

    @Override
    public String toString() {
        return "ProjectCreationRequest{" +
                "uuid=" + getUuid() +
                ", requestor=" + getRequestor() +
                ", businessService=" + businessService +
                '}';
    }
}
