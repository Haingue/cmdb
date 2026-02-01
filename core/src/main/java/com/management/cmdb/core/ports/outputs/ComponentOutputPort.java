package com.management.cmdb.core.ports.outputs;

import com.management.cmdb.core.models.business.component.Component;

import java.util.Optional;
import java.util.UUID;

public interface ComponentOutputPort {

    Optional<Component> findOne (UUID uuid);
    Optional<Component> findOneByName (String name);
    void delete (Component component);
}
