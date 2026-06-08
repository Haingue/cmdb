package com.management.cmdb.backend.configuration;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.technical.ComponentVisitor;
import com.management.cmdb.core.ports.inputs.ComponentInputPort;
import com.management.cmdb.core.ports.inputs.EnvironmentInputPort;
import com.management.cmdb.core.ports.inputs.ProjectInputPort;
import com.management.cmdb.core.ports.outputs.*;
import com.management.cmdb.core.service.BusinessServiceService;
import com.management.cmdb.core.service.ComponentService;
import com.management.cmdb.core.service.EnvironmentService;
import com.management.cmdb.core.service.ProjectService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    private final BusinessServiceOutputPort businessServiceAdapter;
    private final ProjectOutputPort projectAdapter;
    private final EnvironmentOutputPort environmentAdapter;
    private final ComponentOutputPort componentAdapter;
    private final NotificationOutputPort notificationAdapter;
    private final ComponentVisitor<Component> componentVisitor;

    public BeanConfiguration(BusinessServiceOutputPort businessServiceAdapter, ProjectOutputPort projectAdapter, EnvironmentOutputPort environmentAdapter, ComponentOutputPort componentAdapter, NotificationOutputPort notificationAdapter, ComponentVisitor<Component> componentVisitor) {
        this.businessServiceAdapter = businessServiceAdapter;
        this.projectAdapter = projectAdapter;
        this.environmentAdapter = environmentAdapter;
        this.componentAdapter = componentAdapter;
        this.notificationAdapter = notificationAdapter;
        this.componentVisitor = componentVisitor;
    }

    @Bean
    public BusinessServiceService businessServiceService (BusinessServiceOutputPort businessServiceAdapter) {
        return new BusinessServiceService(businessServiceAdapter, notificationAdapter);
    }

    @Bean
    public ComponentInputPort componentService (ComponentOutputPort componentAdapter, ComponentVisitor<Component> componentVisitor) {
        return new ComponentService(componentAdapter, componentVisitor, notificationAdapter);
    }

    @Bean
    public EnvironmentInputPort environmentService (EnvironmentOutputPort environmentAdapter, ComponentInputPort componentService) {
        return new EnvironmentService(environmentAdapter, componentService, notificationAdapter);
    }

    @Bean
    public ProjectInputPort projectService (ProjectOutputPort projectAdapter, BusinessServiceService businessServiceService, EnvironmentInputPort environmentService) {
        return new ProjectService(projectAdapter, businessServiceService, environmentService, notificationAdapter);
    }
}
