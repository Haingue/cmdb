package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.constants.EnvironmentType;
import com.management.cmdb.core.models.business.constants.GlobalStaticParameter;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.inputs.EnvironmentInputPort;
import com.management.cmdb.core.ports.outputs.EnvironmentOutputPort;

import java.util.UUID;

public class EnvironmentService implements EnvironmentInputPort {

    private final EnvironmentOutputPort environmentOutputPort;
    private final ComponentService componentService;

    public EnvironmentService(EnvironmentOutputPort environmentOutputPort, ComponentService componentService) {
        this.environmentOutputPort = environmentOutputPort;
        this.componentService = componentService;
    }

    @Override
    public Environment create(String location, EnvironmentType type, String jiraTracker) {
        Environment environment = Environment.create(location, type, jiraTracker);
        environment.isValid();
        environment = environmentOutputPort.save(environment);

        // Create component
        try {
            environment.getComponents().forEach(componentService::create);
        } catch (CoreException e) {
            environmentOutputPort.delete(environment);
            throw e;
        }

        // TODO notify maintainer

        return environmentOutputPort.save(environment);
    }

    @Override
    public Environment update(Environment environment) {
        if (environment == null) throw new InvalidObjectException("Environment cannot be null");

        Environment existingEnvironment = environmentOutputPort.findOne(environment.getUuid())
                .orElseThrow(() -> new NotFoundException(environment.getUuid()));
        existingEnvironment.setLocation(environment.getLocation());
        existingEnvironment.setType(environment.getType());
        existingEnvironment.setJiraTracker(environment.getJiraTracker());
        existingEnvironment.setStatus(environment.getStatus());

        // TODO update component

        // TODO notify maintainer
        existingEnvironment.update(GlobalStaticParameter.SYSTEM_NAME.name());
        return environmentOutputPort.save(existingEnvironment);
    }

    @Override
    public void archive(UUID uuid) {
        throw new NotImplemented();
    }

    @Override
    public void delete(UUID uuid) {
        Environment existingEnvironment = environmentOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException(uuid.toString()));
        environmentOutputPort.delete(existingEnvironment);
        environmentOutputPort.save(existingEnvironment);
    }

    @Override
    public Environment findOne(UUID uuid) {
        return environmentOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException(uuid));
    }
}
