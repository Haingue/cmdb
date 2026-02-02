package com.management.cmdb.core.models;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.Hardware;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.component.Software;
import com.management.cmdb.core.models.business.component.network.Vlan;
import com.management.cmdb.core.models.business.constant.*;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SimpleModelTest {

    private final Project project;
    private final Environment env1;
    private final Technology flask;
    private final Technology linux;
    private final Technology springBoot;
    private final Host sensorHost;
    private final Host backendHost;
    private final Software backendSoftware;

    public SimpleModelTest() throws UnknownHostException {
        project = Project.builder()
                .fullName("Project Number 1")
                .shortName("Project1")
                .description("Description of Project1")
                .businessService(BusinessService.builder().name("Business 1").abbreviation("B1").build())
                .maintainers(new UserGroup("P1_Maintainers", "", null, null, Set.of()))
                .owners(new UserGroup("P1_Owners", "", null, null, Set.of()))
                .build();
        env1 = Environment.builder().location("Environment 1").type(EnvironmentType.TEST).jiraTracker("key-001").build();
        project.addEnvironment(env1);

        flask = new Technology("Flask", "Framework to create Python API", TechnologyType.BACKEND, "Python", Version.fromString("3.0.0"), Version.fromString("3.9.4"), Version.fromString("3.9.4"));
        linux = new Technology("Linux", "Linux OS", TechnologyType.OPERATING_SYSTEM, null, Version.fromString("18.0.0"), Version.fromString("19.0.0"), Version.fromString("21.0.0"));
        springBoot = new Technology("Spring-Boot", "Framework to create Java API", TechnologyType.BACKEND, "Java", Version.fromString("3.2.0"), Version.fromString("3.5.4"), Version.fromString("3.5.4"));

        sensorHost = Hardware.builder()
                .name("Sensor")
                .description("Sensor desc")
                .type(ComponentType.HARDWARE)
                .version(new Version(3, 8, 5))
                .certificate(null)
                .technology(flask)
                .dns("sensor.com")
                .macAddress("00:00:00:00")
                .ipAddress(InetAddress.getByName("0.0.0.0"))
                .vlan(new Vlan(10, "", null, null))
                .patchingDay(DayOfWeek.SATURDAY)
                .domain(ActiveDirectoryDomainName.COMMON)
                .networkArea(NetworkArea.DMZ)
                .location("Poteau A1")
                .build();
        backendHost = Hardware.builder()
                .name("BackendServer")
                .description("BackendServer desc")
                .type(ComponentType.VIRTUAL_MACHINE)
                .version(new Version(18, 0, 5))
                .certificate(null)
                .technology(linux)
                .dns("backendserver.com")
                .macAddress("00:00:00:00")
                .ipAddress(InetAddress.getByName("0.0.0.0"))
                .vlan(new Vlan(10, "", null, null))
                .patchingDay(DayOfWeek.SATURDAY)
                .domain(ActiveDirectoryDomainName.COMMON)
                .networkArea(NetworkArea.DMZ)
                .location("ComputerRoom 1")
                .build();
        backendSoftware = Software.builder()
                .name("BackendSoftware")
                .description("BackendSoftware desc")
                .type(ComponentType.SOFTWARE)
                .version(new Version(1, 0, 5))
                .certificate(null)
                .technology(springBoot)
                .host(backendHost)
                .build();

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
    public void should_return_one_outdated_component() {
        Environment envTest = project.getEnvironments().stream().findFirst().get();
        Set<Component> outDatedComponents = envTest.getOutDatedComponents();
        Assertions.assertFalse(outDatedComponents.isEmpty());
        Assertions.assertEquals(backendSoftware, outDatedComponents.stream().findFirst().get());
    }

}
