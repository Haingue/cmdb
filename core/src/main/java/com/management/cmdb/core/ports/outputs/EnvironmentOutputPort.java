package com.management.cmdb.core.ports.outputs;

import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;

import java.util.Optional;
import java.util.UUID;

public interface EnvironmentOutputPort {

    Optional<Environment> findOne (UUID uuid);
    Optional<Environment> findByComponent (UUID componentUuid);

    Environment save (Environment environment);

    void delete(Environment environment);
}
