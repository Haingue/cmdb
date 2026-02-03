package com.management.cmdb.core.models.business.request;

import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.Project;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ProjectCreationRequest extends Request {
    private final Project project;
    private final String businessServiceName;

    public ProjectCreationRequest(UUID uuid, User requestor, Instant requestTimestamp, Project project, String businessServiceName) {
        super(uuid, requestor, requestTimestamp);
        this.project = project;
        this.businessServiceName = businessServiceName;
    }

    @Override
    public String toString() {
        return "ProjectCreationRequest{" +
                "uuid=" + getUuid() +
                ", requestor=" + getRequestor() +
                ", businessServiceName=" + businessServiceName +
                '}';
    }
}
