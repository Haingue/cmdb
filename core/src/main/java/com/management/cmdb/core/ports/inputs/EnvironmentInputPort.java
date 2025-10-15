package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.constants.EnvironmentType;
import com.management.cmdb.core.models.business.project.Environment;

import java.util.UUID;

public interface EnvironmentInputPort {

    Environment create (String location, EnvironmentType type, String jiraTracker);
    Environment update (Environment environment);
    void archive (UUID uuid);
    void delete (UUID uuid);

    Environment findOne (UUID uuid);

}
