package com.management.cmdb.core.service;

import com.management.cmdb.core.fake.FakeBusinessService;
import com.management.cmdb.core.fake.FakeUser;
import com.management.cmdb.core.fake.FakeUserGroup;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.ports.outputs.ProjectOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    /*
    private ProjectOutputPort projectOutputPort;
    private BusinessServiceOutputPort businessServiceOutputPort;
    private EnvironmentOutputPort environmentOutputPort;
    private ComponentOutputPort componentOutputPort;

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
        componentService = new ComponentService(componentOutputPort);
        environmentService = new EnvironmentService(environmentOutputPort, componentService);
        projectService = new ProjectService(projectOutputPort, businessServiceService, environmentService);
    }*/


    @Mock
    private ProjectOutputPort projectOutputPort;

    @Mock
    private BusinessServiceService businessServiceService;

    @Mock
    private EnvironmentService environmentService;

    @InjectMocks
    private ProjectService projectService;

    // Utilisateur fictif pour les tests
    private final User initiator = FakeUser.SUPER_ADMIN.user;

    // BusinessService fictif
    private final BusinessService businessService = FakeBusinessService.BUSINESS_SERVICE_1.businessService;

    // UserGroup fictifs
    private final UserGroup maintainers = FakeUserGroup.TECHNICAL_GROUP.userGroup;
    private final UserGroup owners = FakeUserGroup.BUSINESS_GROUP.userGroup;

    // Environnements fictifs
    private final Set<Environment> environments = Set.of(
            Environment.builder().location("Location 1").type(EnvironmentType.ACC).jiraTracker("JIRA-1").build(),
            Environment.builder().location("Location 2").type(EnvironmentType.PROD).jiraTracker("JIRA-2").build()
    );

    // Projet fictif
    private final Project project = Project.builder()
            .fullName("Test Project")
            .shortName("TP")
            .description("Description")
            .maintainers(maintainers)
            .owners(owners)
            .build();

    {
        project.addBusinessService(businessService);
    }

    @Test
    void findOne_ShouldReturnProject_WhenProjectExists() {
        // Given
        UUID projectUuid = UUID.randomUUID();
        given(projectOutputPort.findOne(projectUuid)).willReturn(Optional.of(project));

        // When
        Project result = projectService.findOne(projectUuid, initiator);

        // Then
        assertThat(result).isEqualTo(project);
    }

    @Test
    void findOne_ShouldThrowNotFoundException_WhenProjectDoesNotExist() {
        // Given
        UUID projectUuid = UUID.randomUUID();
        given(projectOutputPort.findOne(projectUuid)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.findOne(projectUuid, initiator))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Object not found: Project with id " + projectUuid.toString() + " not found");
    }

    @Test
    void findOneByShortName_ShouldReturnProject_WhenProjectExists() {
        // Given
        String shortName = "TP";
        given(projectOutputPort.findOneByShortName(shortName)).willReturn(Optional.of(project));

        // When
        Project result = projectService.findOneByShortName(shortName, initiator);

        // Then
        assertThat(result).isEqualTo(project);
    }

    @Test
    void findOneByShortName_ShouldThrowNotFoundException_WhenProjectDoesNotExist() {
        // Given
        String shortName = "UNKNOWN";
        given(projectOutputPort.findOneByShortName(shortName)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.findOneByShortName(shortName, initiator))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Object not found: " + shortName);
    }

    @Test
    void create_ShouldSaveProject_WhenAllParametersAreValid() {
        // Given
        given(businessServiceService.findOne(businessService.getName(), initiator)).willReturn(businessService);
        given(projectOutputPort.save(any(Project.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        Project result = projectService.create(
                project.getFullName(),
                project.getShortName(),
                project.getDescription(),
                businessService,
                maintainers,
                owners,
                environments,
                initiator
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBusinessServices()).contains(businessService);
        assertThat(result.getEnvironments()).hasSameSizeAs(environments);
    }

    @Test
    void create_ShouldRollback_WhenEnvironmentCreationFails() {
        // Given
        given(businessServiceService.findOne(businessService.getName(), initiator)).willReturn(businessService);
        given(projectOutputPort.save(any(Project.class))).willAnswer(invocation -> invocation.getArgument(0));
        willThrow(new CoreException("Environment creation failed")).given(environmentService).create(UUID.randomUUID(), anyString(), anyString(), anyString(), EnvironmentType.TEST, anyString(), any(User.class));

        // When & Then
        assertThatThrownBy(() -> projectService.create(
                project.getFullName(),
                project.getShortName(),
                project.getDescription(),
                businessService,
                maintainers,
                owners,
                environments,
                initiator
        )).isInstanceOf(CoreException.class).hasMessage("Environment creation failed");

        verify(projectOutputPort, times(1)).delete(any(UUID.class));
    }

    @Test
    void update_ShouldUpdateProject_WhenAllParametersAreValid() {
        // Given
        UUID projectUuid = UUID.randomUUID();
        given(projectOutputPort.findOne(projectUuid)).willReturn(Optional.of(project));
        given(businessServiceService.findOne(businessService.getName(), initiator)).willReturn(businessService);
        given(projectOutputPort.save(any(Project.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        Project updatedProject = Project.builder()
                .fullName("Updated Project")
                .shortName("UP")
                .description("Updated Description")
                .maintainers(maintainers)
                .owners(owners)
                .build();
        updatedProject.addBusinessService(businessService);
        Project result = projectService.update(updatedProject, initiator);

        // Then
        assertThat(result.getFullName()).isEqualTo("Updated Project");
        assertThat(result.getShortName()).isEqualTo("UP");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void archive_ShouldSetArchiveDatetime_WhenProjectExists() {
        // Given
        UUID projectUuid = UUID.randomUUID();
        given(projectOutputPort.findOne(projectUuid)).willReturn(Optional.of(project));
        given(projectOutputPort.save(any(Project.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        projectService.archive(projectUuid, initiator);

        // Then
        assertThat(project.getArchiveDatetime()).isNotNull();
    }

    @Test
    void delete_ShouldArchiveProjectAndDeleteEnvironments_WhenProjectExists() {
        // Given
        UUID projectUuid = UUID.randomUUID();
        project.addEnvironment(Environment.builder().location("Location 1").type(EnvironmentType.TEST).jiraTracker("JIRA-1").build());
        given(projectOutputPort.findOne(projectUuid)).willReturn(Optional.of(project));
        given(projectOutputPort.save(any(Project.class))).willAnswer(invocation -> invocation.getArgument(0));
        willDoNothing().given(environmentService).delete(any(UUID.class), any(User.class));

        // When
        projectService.delete(projectUuid, initiator);

        // Then
        assertThat(project.getArchiveDatetime()).isNotNull();
        verify(environmentService, times(1)).delete(any(UUID.class), any(User.class));
    }
}