package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;

import java.util.Set;
import java.util.UUID;

public interface ProjectInputPort {

    Project create (String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners, Set<Environment>environments);
    Project update (Project project);
    void archive (UUID uuid);
    void delete (UUID uuid);

    Project findOneByUuid(UUID uuid);
    Project findOneByShortName(String shortName);

}
