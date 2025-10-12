package com.management.cmdb.core.models;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.Hardware;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.component.Software;
import com.management.cmdb.core.models.business.constants.ActiveDirectoryDomainName;
import com.management.cmdb.core.models.business.constants.EnvironmentType;
import com.management.cmdb.core.models.business.constants.NetworkArea;
import com.management.cmdb.core.models.business.constants.TechnologyType;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.DayOfWeek;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SimpleModelTest {
    private final Project project;
    private final Environment env1;
    private final Technology flask = new Technology("Flask", "Framework to create Python API", TechnologyType.BACKEND, Optional.of("Python"), Version.fromString("3.0.0"), Version.fromString("3.9.4"), Version.fromString("3.9.4"));
    private final Technology linux = new Technology("Linux", "Linux OS", TechnologyType.OPERATING_SYSTEM, Optional.empty(), Version.fromString("18.0.0"), Version.fromString("19.0.0"), Version.fromString("21.0.0"));
    private final Technology springBoot = new Technology("Spring-Boot", "Framework to create Java API", TechnologyType.BACKEND, Optional.of("Java"), Version.fromString("3.2.0"), Version.fromString("3.5.4"), Version.fromString("3.5.4"));
    private final Host sensorHost;
    private final Host backendHost;
    private final Software backendSoftware;


    public SimpleModelTest() throws UnknownHostException {
        project = Project.create("Project Number 1", "Project1", "Description of Project1", new BusinessService("Business 1", "B1"), new UserGroup("P1_Maintainers", "", null, null, Set.of()), new UserGroup("P1_Owners", "", null, null, Set.of()));
        env1 = Environment.create("Environment 1", EnvironmentType.TEST, "key-001");
        project.addEnvironment(env1);

        sensorHost = new Hardware(UUID.randomUUID(), "Sensor", "Sensor desc", new Version(3, 8, 5), null, flask, "sensor", "sensor.com", "00:00:00:00", InetAddress.getByName("0.0.0.0"), "10", DayOfWeek.SATURDAY, "France", ActiveDirectoryDomainName.COMMON, NetworkArea.DMZ);
        backendHost = new Hardware(UUID.randomUUID(), "BackendServer", "BackendServer desc", new Version(18, 0, 5), null, linux, "backend", "backend.com", "00:00:00:00", InetAddress.getByName("0.0.0.0"), "10", DayOfWeek.SATURDAY, "France", ActiveDirectoryDomainName.COMMON, NetworkArea.DMZ);
        backendSoftware = new Software(UUID.randomUUID(), "BackendSoftware", "BackendSoftware desc", Version.fromString("1.0.0"), null, springBoot, backendHost);

        env1.addComponents(sensorHost);
        env1.addComponents(backendSoftware);
    }

    @Test
    public void should_return_simple_webapp_project() {
        Assertions.assertNotNull(project.getFullName());
        Assertions.assertNotNull(project.getShortName());
        Assertions.assertNotNull(project.getDescription());
        Assertions.assertNotNull(project.getBusinessService());
        Assertions.assertNotNull(project.getEnvironments());
        Assertions.assertFalse(project.getEnvironments().isEmpty());
        Assertions.assertNotNull(project.getMaintainers());
        Assertions.assertNotNull(project.getOwners());

        Environment envTest = project.getEnvironments().stream().findFirst().get();
        Assertions.assertEquals(env1, envTest);

        Assertions.assertFalse(envTest.getComponents().isEmpty());
        Assertions.assertEquals(sensorHost, envTest.getComponents().stream().filter(component -> component.equals(sensorHost)).findFirst().get());
    }

    @Test
    public void should_return_failed_to_mutate_environment() {
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                project.getEnvironments().add(env1)
        );
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                env1.getComponents().add(backendSoftware)
        );
    }

    @Test
    public void should_return_one_outdated_component() {
        Environment envTest = project.getEnvironments().stream().findFirst().get();
        Set<Component> outDatedComponents = envTest.getOutDatedComponents();
        Assertions.assertFalse(outDatedComponents.isEmpty());
        Assertions.assertEquals(backendSoftware, outDatedComponents.stream().findFirst().get());
    }

}
