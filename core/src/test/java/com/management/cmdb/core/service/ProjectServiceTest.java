package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.ports.outputs.BusinessServiceOutputPort;
import com.management.cmdb.core.ports.outputs.EnvironmentOutputPort;
import com.management.cmdb.core.ports.outputs.IdentityOutputPort;
import com.management.cmdb.core.ports.outputs.ProjectOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    private ProjectOutputPort projectOutputPort;
    private BusinessServiceOutputPort businessServiceOutputPort;
    private EnvironmentOutputPort environmentOutputPort;

    private BusinessServiceService businessServiceService;
    private EnvironmentService environmentService;
    private ComponentService componentService;
    private ProjectService projectService;

    @BeforeEach
    public void setUp() {
        projectOutputPort = Mockito.mock(ProjectOutputPort.class);
        businessServiceOutputPort = Mockito.mock(BusinessServiceOutputPort.class);
        environmentOutputPort = Mockito.mock(EnvironmentOutputPort.class);
        businessServiceService = new BusinessServiceService(businessServiceOutputPort);
        componentService = new ComponentService();
        environmentService = new EnvironmentService(environmentOutputPort, componentService);
        projectService = new ProjectService(projectOutputPort, businessServiceService, environmentService);
    }


}