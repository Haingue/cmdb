package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.inputs.ComponentInputPort;
import com.management.cmdb.core.ports.outputs.ComponentOutputPort;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class ComponentService implements ComponentInputPort {

    private final ComponentOutputPort componentOutputPort;

    public ComponentService(ComponentOutputPort componentOutputPort) {
        this.componentOutputPort = componentOutputPort;
    }

    @Override
    public Component findOne(UUID uuid, User initiator) {
        return componentOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException("Component with id " + uuid.toString() + " not found"));
    }

    @Override
    public Component create(Component component, User initiator) {
         // TODO check initiator permissions
        component.checkIntegrity();

        Optional<Component> existingComponent = this.componentOutputPort.findOneByName(component.getName());
        if (existingComponent.isPresent()) {
            throw new InvalidObjectException("Component with name " + component.getName() + " already exists", component);
        }
        return this.componentOutputPort.save(component);
    }

    @Override
    public Component update(Component component, User initiator) {
        // TODO check initiator permissions
        component.checkIntegrity();

        Component existingComponent = this.componentOutputPort.findOne(component.getUuid())
                .orElseThrow(() -> new NotFoundException("Component with uuid " + component.getUuid() + " not found"));
        existingComponent = existingComponent.updateFrom(component);
        return this.componentOutputPort.save(existingComponent);
    }

    @Override
    public void archive(UUID uuid, User initiator) {
        // TODO check initiator permissions
        Component existingComponent = this.componentOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException("Component with uuid " + uuid + " not found"));
        existingComponent.setArchiveDatetime(LocalDateTime.now());
        this.componentOutputPort.save(existingComponent);
    }

    @Override
    public void delete(UUID uuid, User initiator) {
        // TODO check initiator permissions
        Component existingComponent = this.componentOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException("Component with uuid " + uuid + " not found"));
        this.componentOutputPort.delete(existingComponent);
    }
}
