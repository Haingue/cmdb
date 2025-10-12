package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.project.Project;

import java.util.UUID;

public interface ProjectInputPort {

    Project create (Project project);
    Project update (Project project);
    void archive (UUID uuid);
    void delete (UUID uuid);

    Project findOneByUuid(UUID uuid);
    Project findOneByShortName(String shortName);

}
