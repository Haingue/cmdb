package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.inputs.ComponentInputPort;

import java.util.UUID;

public class ComponentService implements ComponentInputPort {
    @Override
    public Component create(Component component) {
        throw new NotImplemented();
    }

    @Override
    public Component update(Component component) {
        throw new NotImplemented();
    }

    @Override
    public void archive(UUID uuid) {
        throw new NotImplemented();
    }

    @Override
    public void delete(UUID uuid) {
        throw new NotImplemented();
    }

    @Override
    public Component findOneByUuid(UUID uuid) {
        throw new NotImplemented();
    }
}
