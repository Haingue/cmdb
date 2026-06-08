package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.Notification;
import com.management.cmdb.core.models.business.NotificationType;
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
import com.management.cmdb.core.ports.inputs.ComponentInputPort;
import com.management.cmdb.core.ports.inputs.EnvironmentInputPort;
import com.management.cmdb.core.ports.outputs.EnvironmentOutputPort;
import com.management.cmdb.core.ports.outputs.NotificationOutputPort;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class EnvironmentService implements EnvironmentInputPort {

    private final EnvironmentOutputPort environmentOutputPort;
    private final ComponentInputPort componentService;
    private final NotificationOutputPort notificationOutputPort;

    public EnvironmentService(EnvironmentOutputPort environmentOutputPort, ComponentInputPort componentInputPort, NotificationOutputPort notificationOutputPort) {
        this.environmentOutputPort = environmentOutputPort;
        this.componentService = componentInputPort;
        this.notificationOutputPort = notificationOutputPort;
    }

    @Override
    public Environment findOne(UUID uuid, User initiator) {
        return environmentOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException(uuid));
    }

    @Override
    public Environment create(UUID projectUuid, String projectAbbreviation, String description, String location, EnvironmentType type, String jiraTracker, User initiator) {
        Environment environment = Environment.builder()
                .location(location)
                .name("%s-%s-%s".formatted(projectAbbreviation, location, type))
                .description(description)
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
        newEntity = environmentOutputPort.save(newEntity);
        notificationOutputPort.notify(Notification.builder()
                .title(newEntity.getName())
                .type(NotificationType.ITEM_CREATED)
                .source(initiator.toString())
                .timestamp(Instant.now())
                .payload(newEntity)
                .build()
        );
        return newEntity;
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
        existingEnvironment = environmentOutputPort.save(existingEnvironment);
        notificationOutputPort.notify(Notification.builder()
                .title(existingEnvironment.getName())
                .type(NotificationType.ITEM_UPDATED)
                .source(initiator.toString())
                .timestamp(Instant.now())
                .payload(existingEnvironment)
                .build()
        );
        return existingEnvironment;
    }

    @Override
    public void archive(UUID uuid, User initiator) {
        if (uuid == null) throw new InvalidObjectException("Uuid cannot be null");

        Environment existingEnvironment = environmentOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException(uuid));
        existingEnvironment.setArchiveDatetime(LocalDateTime.now());
        existingEnvironment = this.environmentOutputPort.save(existingEnvironment);
        notificationOutputPort.notify(Notification.builder()
                .title(existingEnvironment.getName())
                .type(NotificationType.ITEM_ARCHIVED)
                .source(initiator.toString())
                .timestamp(Instant.now())
                .payload(existingEnvironment)
                .build());
    }

    @Override
    public void delete(UUID uuid, User initiator) {
        Environment existingEnvironment = environmentOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException(uuid.toString()));
        environmentOutputPort.delete(existingEnvironment);
        environmentOutputPort.save(existingEnvironment);

        notificationOutputPort.notify(Notification.builder()
                .title(existingEnvironment.getName())
                .type(NotificationType.ITEM_DELETED)
                .source(initiator.toString())
                .timestamp(Instant.now())
                .payload(existingEnvironment)
                .build()
        );
    }

}
