package com.management.cmdb.core.ports.outputs;

import com.management.cmdb.core.models.business.component.Component;

import java.util.Optional;
import java.util.UUID;

public interface ComponentOutputPort {

    Optional<Component> findOne (UUID componentId);
    Optional<Component> findOneByName (String name);
    Component save (Component component);
    void delete (Component componentId);
}
