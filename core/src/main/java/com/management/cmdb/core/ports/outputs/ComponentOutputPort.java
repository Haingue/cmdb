package com.management.cmdb.core.ports.outputs;

import com.management.cmdb.core.models.business.component.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComponentOutputPort {

    Optional<Component> findOne (UUID uuid);
    Optional<Component> findOneByName (String name);
    List<Component> findAllByTechnology(String technologyName);
    void delete (Component component);
}
