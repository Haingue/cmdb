package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.business.constant.GlobalStaticParameter;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.request.ComponentCreationRequest;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.inputs.ComponentInputPort;
import com.management.cmdb.core.ports.inputs.EnvironmentInputPort;
import com.management.cmdb.core.ports.outputs.EnvironmentOutputPort;

import java.util.UUID;

public class EnvironmentService implements EnvironmentInputPort {

    private final EnvironmentOutputPort environmentOutputPort;
    private final ComponentInputPort componentService;

    public EnvironmentService(EnvironmentOutputPort environmentOutputPort, ComponentInputPort componentInputPort) {
        this.environmentOutputPort = environmentOutputPort;
        this.componentService = componentInputPort;
    }

    @Override
    public Environment findOne(UUID uuid, User initiator) {
        return environmentOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException(uuid));
    }

    @Override
    public Environment create(UUID projectUuid, String location, EnvironmentType type, String jiraTracker, User initiator) {
        Environment environment = Environment.builder()
                .location(location)
                .status(EnvironmentStatus.REQUESTED)
                .type(type)
                .jiraTracker(jiraTracker)
                .build();
        environment = this.create(environment, initiator);

        // Attach environment to project
        environmentOutputPort.attachProject(environment, projectUuid);
        return environment;
    }

    @Override
    public Environment create(Environment newEntity, User initiator) {
        newEntity.checkIntegrity();
        newEntity = environmentOutputPort.save(newEntity);

        // Create component
        try {
            // TODO save manually environment instead of save project (implicit -> explicit, no JPA/Hibernate)
            newEntity.getComponents().forEach(component -> componentService.create(component, initiator));
        } catch (CoreException e) {
            environmentOutputPort.delete(newEntity);
            throw e;
        }

        // TODO notify maintainer

        return environmentOutputPort.save(newEntity);
    }

    public Component handleAddComponentRequest (ComponentCreationRequest componentCreationRequest) {
        assert componentCreationRequest != null;
        assert componentCreationRequest.getEnvironmentUuid() != null;
        assert componentCreationRequest.getComponent() != null;

        Environment environment = environmentOutputPort.findOne(componentCreationRequest.getEnvironmentUuid())
                .orElseThrow(() -> new NotFoundException(componentCreationRequest.getEnvironmentUuid()));

        Component component = componentService.create(componentCreationRequest.getComponent(), componentCreationRequest.getRequestor());
        environment.getComponents().add(component);
        this.update(environment, componentCreationRequest.getRequestor());
        return component;
    }

    @Override
    public Environment update(Environment environment, User initiator) {
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
    public void archive(UUID uuid, User initiator) {
        throw new NotImplemented();
    }

    @Override
    public void delete(UUID uuid, User initiator) {
        Environment existingEnvironment = environmentOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException(uuid.toString()));
        environmentOutputPort.delete(existingEnvironment);
        environmentOutputPort.save(existingEnvironment);
    }

}
