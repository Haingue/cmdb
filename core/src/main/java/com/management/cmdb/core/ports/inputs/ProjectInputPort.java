package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.ports.inputs.technical.CrudOperation;

import java.util.Set;
import java.util.UUID;

public interface ProjectInputPort extends CrudOperation<Project, UUID> {

    Project create (String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners, Set<Environment>environments, User initiator);
    Project update (Project project, User initiator);
    void archive (UUID uuid, User initiator);
    void delete (UUID uuid, User initiator);

    Project findOneByUuid(UUID uuid, User initiator);
    Project findOneByShortName(String shortName, User initiator);

}
