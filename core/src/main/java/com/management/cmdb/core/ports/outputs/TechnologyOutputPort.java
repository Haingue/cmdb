package com.management.cmdb.core.ports.outputs;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.technology.Technology;

import java.util.List;
import java.util.Optional;

public interface TechnologyOutputPort {

    Optional<Technology> findOne(String name);
    Optional<Technology> findByName(String name);
    List<Technology> findAll();
    List<Component> findComponentsByTechnology(String technologyName);

    Technology save(Technology technology);

    void delete(String name);
}
