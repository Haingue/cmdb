package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.project.Environment;

import java.util.UUID;

public interface EnvironmentInputPort {

    Environment create (Environment environment);
    Environment update (Environment environment);
    void archive (UUID uuid);
    void delete (UUID uuid);

    Environment findOne (Environment environment);

}
