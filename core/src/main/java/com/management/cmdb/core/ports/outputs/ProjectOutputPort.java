package com.management.cmdb.core.ports.outputs;

import com.management.cmdb.core.models.business.project.Project;

import java.util.Optional;
import java.util.UUID;

public interface ProjectOutputPort {

    Optional<Project> findOne (UUID uuid);
    Optional<Project> findOneByShortName (String shortName);
    Optional<Project> findByEnvironment (UUID uuid);

    Project save (Project project);

    void delete (UUID uuid);

}
