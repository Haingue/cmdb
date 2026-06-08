package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.ports.inputs.TechnologyInputPort;
import com.management.cmdb.core.ports.outputs.ComponentOutputPort;
import com.management.cmdb.core.ports.outputs.NotificationOutputPort;
import com.management.cmdb.core.ports.outputs.TechnologyOutputPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TechnologyService implements TechnologyInputPort {

    private final TechnologyOutputPort technologyOutputPort;
    private final ComponentOutputPort componentOutputPort;
    private final NotificationOutputPort notificationOutputPort;

    public TechnologyService(TechnologyOutputPort technologyOutputPort, ComponentOutputPort componentOutputPort, NotificationOutputPort notificationOutputPort) {
        this.technologyOutputPort = technologyOutputPort;
        this.componentOutputPort = componentOutputPort;
        this.notificationOutputPort = notificationOutputPort;
    }

    @Override
    public Technology findOne(String name, User initiator) {
        return technologyOutputPort.findOne(name)
                .orElseThrow(() -> new NotFoundException("Technology with name " + name + " not found"));
    }

    @Override
    public Technology findByName(String name) {
        return technologyOutputPort.findByName(name)
                .orElseThrow(() -> new NotFoundException("Technology with name " + name + " not found"));
    }

    @Override
    public Technology create(Technology technology) {
        if (technology == null) {
            throw new InvalidObjectException("Technology cannot be null");
        }
        technology.checkIntegrity();

        Optional<Technology> existing = technologyOutputPort.findByName(technology.getName());
        if (existing.isPresent()) {
            throw new InvalidObjectException("Technology with name " + technology.getName() + " already exists", technology);
        }

        return technologyOutputPort.save(technology);
    }

    @Override
    public Technology update(Technology technology) {
        if (technology == null) {
            throw new InvalidObjectException("Technology cannot be null");
        }
        technology.checkIntegrity();

        Technology existing = technologyOutputPort.findByName(technology.getName())
                .orElseThrow(() -> new NotFoundException("Technology with name " + technology.getName() + " not found"));

        existing.setName(technology.getName());
        existing.setDescription(technology.getDescription());
        existing.setType(technology.getType());
        existing.setProgrammingLanguage(technology.getProgrammingLanguage());
        existing.setMinimalVersion(technology.getMinimalVersion());
        existing.setTargetVersion(technology.getTargetVersion());
        existing.setLastVersion(technology.getLastVersion());

        return technologyOutputPort.save(existing);
    }

    @Override
    public void archive(String name) {
        Technology technology = technologyOutputPort.findOne(name)
                .orElseThrow(() -> new NotFoundException("Technology with name " + name + " not found"));
        technologyOutputPort.save(technology);
    }

    @Override
    public void delete(String name) {
        Technology technology = technologyOutputPort.findOne(name)
                .orElseThrow(() -> new NotFoundException("Technology with name " + name + " not found"));
        technologyOutputPort.delete(name);
    }

    @Override
    public List<Component> setMinimalVersionAndScanAssets (String name, Version newMinimalVersion) {
        if (name == null || newMinimalVersion == null) {
            throw new InvalidObjectException("Technology name and version cannot be null");
        }

        Technology existing = technologyOutputPort.findByName(name)
                .orElseThrow(() -> new NotFoundException("Technology with name " + name + " not found"));

        if (existing.getMinimalVersion() != null) {
            existing.setMinimalVersion(newMinimalVersion);
        }

        existing.checkIntegrity();

        Technology saved = technologyOutputPort.save(existing);

        List<Component> components = componentOutputPort.findAllByTechnology(saved.getName());
        return components.stream()
                .filter(component -> component.getVersion().isOlderThan(newMinimalVersion))
                .collect(Collectors.toList());
    }

    @Override
    public Technology create(Technology newEntity, User initiator) {
        return create(newEntity);
    }

    @Override
    public Technology update(Technology environment, User initiator) {
        return update(environment);
    }

    @Override
    public void archive(String uuid, User initiator) {
        archive(uuid);
    }

    @Override
    public void delete(String uuid, User initiator) {
        delete(uuid);
    }
}
