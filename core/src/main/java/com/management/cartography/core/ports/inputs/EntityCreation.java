package com.management.cartography.core.ports.inputs;

import com.management.cartography.core.models.business.component.Component;
import com.management.cartography.core.models.business.project.BusinessService;
import com.management.cartography.core.models.business.project.Environment;
import com.management.cartography.core.models.business.project.Project;
import com.management.cartography.core.models.business.technology.Technology;

public interface EntityCreation {

    BusinessService createBusinessService (BusinessService businessService);
    Project createProject (Project project);
    Environment createEnvironment (Environment environment);
    Component createComponent (Component component);
    Technology createTechnology (Technology technology);

}
