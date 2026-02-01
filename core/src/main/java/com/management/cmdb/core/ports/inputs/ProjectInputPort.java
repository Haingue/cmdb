package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.business.request.EnvironmentCreationRequest;
import com.management.cmdb.core.models.business.request.ProjectCreationRequest;
import com.management.cmdb.core.ports.inputs.technical.CrudOperation;

import java.util.Set;
import java.util.UUID;

public interface ProjectInputPort extends CrudOperation<Project, UUID> {

    Project handleProjectCreationRequest(ProjectCreationRequest projectCreationRequest);
    Environment handleAddEnvironmentRequest (EnvironmentCreationRequest environmentCreationRequest);

    Project findOneByShortName(String shortName, User initiator);

}
