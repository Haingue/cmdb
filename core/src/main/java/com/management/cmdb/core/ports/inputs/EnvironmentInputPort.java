package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.ports.inputs.technical.CrudOperation;

import java.util.UUID;

public interface EnvironmentInputPort extends CrudOperation<Environment, UUID> {

    Environment create (String location, EnvironmentType type, String jiraTracker, User initiator);

}
