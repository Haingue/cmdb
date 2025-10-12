package com.management.cartography.core.ports.inputs.search;

import com.management.cartography.core.models.business.component.Component;
import com.management.cartography.core.models.business.constants.TechnologyType;
import com.management.cartography.core.models.business.project.BusinessService;
import com.management.cartography.core.models.business.project.Environment;
import com.management.cartography.core.models.business.project.Project;
import com.management.cartography.core.models.business.technology.Technology;

import java.util.Set;
import java.util.UUID;

public interface EntitySearch {

    BusinessService findOneBusinessServiceByName(String name);

    Project findOneProjectByShortName(String shortName);

    Set<Environment> searchEnvironmentByProjectShortName(String projectShortName);
    Environment findOneEnvironment(UUID id);

    Set<Component> searchComponentByProjectShortName(String projectShortName);
    Component findOneComponent(UUID uuid);

    Technology findOneTechnology(String name);
    Set<Technology> searchTechnologyByType(TechnologyType type);
    Set<Technology> searchTechnologyByProgrammingLanguage(String language);

}
