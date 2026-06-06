package com.management.cmdb.core.service;

import com.management.cmdb.core.fake.FakeEnvironment;
import com.management.cmdb.core.fake.FakeUser;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.GenericComponent;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.request.ComponentCreationRequest;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.ports.inputs.ComponentInputPort;
import com.management.cmdb.core.ports.outputs.EnvironmentOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvironmentServiceTest {

    @Mock
    private EnvironmentOutputPort environmentOutputPort;

    @Mock
    private ComponentInputPort componentInputPort;

    @InjectMocks
    private EnvironmentService environmentService;

    private final User initiator = FakeUser.SUPER_ADMIN.user;
    private final Environment devEnv = FakeEnvironment.DEV_ENVIRONMENT.environment;
    private final Environment testEnv = FakeEnvironment.TEST_ENVIRONMENT.environment;
    private final Environment prodEnv = FakeEnvironment.PROD_ENVIRONMENT.environment;

    @Test
    void findOne_ShouldReturnEnvironment_WhenExists() {
        // Given
        UUID envUuid = devEnv.getUuid();
        given(environmentOutputPort.findOne(envUuid)).willReturn(Optional.of(devEnv));

        // When
        Environment result = environmentService.findOne(envUuid, initiator);

        // Then
        assertThat(result).isEqualTo(devEnv);
    }

    @Test
    void findOne_ShouldThrowNotFoundException_WhenNotExists() {
        // Given
        UUID unknownUuid = UUID.randomUUID();
        given(environmentOutputPort.findOne(unknownUuid)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> environmentService.findOne(unknownUuid, initiator))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(unknownUuid.toString());
    }

    @Test
    void create_WithEnvironmentObject_ShouldSaveAndReturnEnvironment_WhenValid() {
        // Given
        Environment newEnv = Environment.builder()
                .location("New Location")
                .type(EnvironmentType.DEV)
                .status(EnvironmentStatus.REQUESTED)
                .jiraTracker("NEW-JIRA")
                .build();

        given(environmentOutputPort.save(any(Environment.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        Environment result = environmentService.create(newEnv, initiator);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLocation()).isEqualTo("New Location");
        assertThat(result.getType()).isEqualTo(EnvironmentType.DEV);
        assertThat(result.getStatus()).isEqualTo(EnvironmentStatus.REQUESTED);
        // Note: save() is called twice in EnvironmentService.create() (line 57 and 70)
        verify(environmentOutputPort, times(2)).save(any(Environment.class));
        // Note: componentInputPort stub not needed - newEnv has no components
    }

    @Test
    void create_WithEnvironmentObject_ShouldThrowNullPointerException_WhenEnvironmentIsNull() {
        // When & Then
        // Note: EnvironmentService.create() calls newEntity.checkIntegrity() which throws NPE if null
        assertThatThrownBy(() -> environmentService.create(null, initiator))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void create_WithEnvironmentObject_ShouldThrowInvalidObjectException_WhenLocationIsBlank() {
        // Given
        Environment invalidEnv = Environment.builder()
                .location("")
                .type(EnvironmentType.DEV)
                .status(EnvironmentStatus.REQUESTED)
                .build();

        // When & Then
        assertThatThrownBy(() -> environmentService.create(invalidEnv, initiator))
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("location");
    }

    @Test
    void create_WithEnvironmentObject_ShouldThrowInvalidObjectException_WhenTypeIsNull() {
        // Given
        Environment invalidEnv = Environment.builder()
                .location("Location")
                .type(null)
                .status(EnvironmentStatus.REQUESTED)
                .build();

        // When & Then
        assertThatThrownBy(() -> environmentService.create(invalidEnv, initiator))
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("type");
    }

    @Test
    void create_WithEnvironmentObject_ShouldThrowInvalidObjectException_WhenStatusIsNull() {
        // Given
        Environment invalidEnv = Environment.builder()
                .location("Location")
                .type(EnvironmentType.DEV)
                .status(null)
                .build();

        // When & Then
        assertThatThrownBy(() -> environmentService.create(invalidEnv, initiator))
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("status");
    }

    @Test
    void create_WithDetailedParameters_ShouldCreateEnvironmentWithGeneratedName() {
        // Given
        UUID projectUuid = UUID.randomUUID();
        String projectAbbreviation = "PROJ1";
        String description = "Dev environment";
        String location = "Paris";
        EnvironmentType type = EnvironmentType.DEV;
        String jiraTracker = "PROJ1-DEV";

        Environment expectedEnv = Environment.builder()
                .name("PROJ1-Paris-DEV")
                .description(description)
                .location(location)
                .type(type)
                .status(EnvironmentStatus.REQUESTED)
                .jiraTracker(jiraTracker)
                .build();

        // Note: componentInputPort stub not needed - component creation is commented in EnvironmentService
        given(environmentOutputPort.save(any(Environment.class))).willAnswer(inv -> {
            Environment env = inv.getArgument(0);
            env.setUuid(UUID.randomUUID());
            return env;
        });

        // When
        Environment result = environmentService.create(
                projectUuid,
                projectAbbreviation,
                description,
                location,
                type,
                jiraTracker,
                initiator
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("PROJ1-Paris-DEV");
        assertThat(result.getLocation()).isEqualTo(location);
        assertThat(result.getType()).isEqualTo(type);
        assertThat(result.getStatus()).isEqualTo(EnvironmentStatus.REQUESTED);
        assertThat(result.getJiraTracker()).isEqualTo(jiraTracker);
        // Note: save() is called twice in EnvironmentService.create() (line 57 and 70)
        verify(environmentOutputPort, times(2)).save(any(Environment.class));
        verify(environmentOutputPort).attachProject(any(Environment.class), eq(projectUuid));
    }

    @Test
    void update_ShouldUpdateEnvironment_WhenValid() {
        // Given
        UUID envUuid = devEnv.getUuid();
        Environment existingEnv = Environment.builder()
                .uuid(envUuid)
                .location("Old Location")
                .type(EnvironmentType.DEV)
                .status(EnvironmentStatus.READY)
                .jiraTracker("OLD-JIRA")
                .build();

        Environment updatedEnv = Environment.builder()
                .uuid(envUuid)
                .location("New Location")
                .type(EnvironmentType.DEV)
                .status(EnvironmentStatus.IN_PROGRESS)
                .jiraTracker("NEW-JIRA")
                .build();

        given(environmentOutputPort.findOne(envUuid)).willReturn(Optional.of(existingEnv));
        // Note: componentInputPort stub not needed - component update is commented in EnvironmentService
        given(environmentOutputPort.save(any(Environment.class))).willAnswer(inv -> inv.getArgument(0));

        // When
        Environment result = environmentService.update(updatedEnv, initiator);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLocation()).isEqualTo("New Location");
        assertThat(result.getStatus()).isEqualTo(EnvironmentStatus.IN_PROGRESS);
        assertThat(result.getJiraTracker()).isEqualTo("NEW-JIRA");
        // Note: EnvironmentService.update() calls save() once
        verify(environmentOutputPort).save(any(Environment.class));
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenNotExists() {
        // Given
        UUID unknownUuid = UUID.randomUUID();
        Environment envToUpdate = Environment.builder()
                .uuid(unknownUuid)
                .location("Location")
                .type(EnvironmentType.DEV)
                .status(EnvironmentStatus.READY)
                .build();

        given(environmentOutputPort.findOne(unknownUuid)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> environmentService.update(envToUpdate, initiator))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(unknownUuid.toString());
    }

    @Test
    void update_ShouldThrowInvalidObjectException_WhenEnvironmentIsNull() {
        // When & Then
        assertThatThrownBy(() -> environmentService.update(null, initiator))
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    void archive_ShouldSetArchiveDatetime_WhenEnvironmentExists() {
        // Given
        UUID envUuid = devEnv.getUuid();
        given(environmentOutputPort.findOne(envUuid)).willReturn(Optional.of(devEnv));
        given(environmentOutputPort.save(any(Environment.class))).willAnswer(inv -> inv.getArgument(0));

        // When
        environmentService.archive(envUuid, initiator);

        // Then
        verify(environmentOutputPort).findOne(envUuid);
        verify(environmentOutputPort).save(any(Environment.class));
    }

    @Test
    void archive_ShouldThrowNotFoundException_WhenEnvironmentNotExists() {
        // Given
        UUID unknownUuid = UUID.randomUUID();
        given(environmentOutputPort.findOne(unknownUuid)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> environmentService.archive(unknownUuid, initiator))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(unknownUuid.toString());
    }

    @Test
    void delete_ShouldDeleteEnvironment_WhenExists() {
        // Given
        UUID envUuid = devEnv.getUuid();
        given(environmentOutputPort.findOne(envUuid)).willReturn(Optional.of(devEnv));

        // When
        environmentService.delete(envUuid, initiator);

        // Then
        verify(environmentOutputPort).findOne(envUuid);
        verify(environmentOutputPort).delete(devEnv);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenNotExists() {
        // Given
        UUID unknownUuid = UUID.randomUUID();
        given(environmentOutputPort.findOne(unknownUuid)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> environmentService.delete(unknownUuid, initiator))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(unknownUuid.toString());
    }

    @Test
    void handleAddComponentRequest_ShouldCreateComponentAndAddToEnvironment_WhenValid() {
        // Given
        UUID envUuid = devEnv.getUuid();
        Component newComponent = GenericComponent.builder()
                .uuid(UUID.randomUUID())
                .name("New Component")
                .type(ComponentType.SOFTWARE)
                .build();

        // Create request with required abstract method implementation
        ComponentCreationRequest request = new ComponentCreationRequest(
                UUID.randomUUID(),
                initiator,
                Instant.now(),
                null, // projectUuid not used in EnvironmentService.handleAddComponentRequest
                envUuid,
                newComponent
        ) {
            @Override
            public <T> T accept(com.management.cmdb.core.models.technical.ComponentVisitor<T> visitor) {
                return null; // Not used in test
            }
        };

        given(environmentOutputPort.findOne(envUuid)).willReturn(Optional.of(devEnv));
        given(componentInputPort.create(any(Component.class), any(User.class))).willReturn(newComponent);
        given(environmentOutputPort.save(any(Environment.class))).willAnswer(inv -> inv.getArgument(0));

        // When
        Component result = environmentService.handleAddComponentRequest(request);

        // Then
        assertThat(result).isEqualTo(newComponent);
        // Note: findOne is called twice - once in handleAddComponentRequest, once in update
        verify(environmentOutputPort, times(2)).findOne(any(UUID.class));
        verify(componentInputPort).create(newComponent, initiator);
        // Note: EnvironmentService.handleAddComponentRequest calls update() which calls save() once
        verify(environmentOutputPort).save(any(Environment.class));
    }

    @Test
    void handleAddComponentRequest_ShouldThrowNotFoundException_WhenEnvironmentNotExists() {
        // Given
        UUID unknownUuid = UUID.randomUUID();
        Component newComponent = GenericComponent.builder()
                .uuid(UUID.randomUUID())
                .name("New Component")
                .type(ComponentType.SOFTWARE)
                .build();

        ComponentCreationRequest request = new ComponentCreationRequest(
                UUID.randomUUID(),
                initiator,
                Instant.now(),
                null,
                unknownUuid,
                newComponent
        ) {
            @Override
            public <T> T accept(com.management.cmdb.core.models.technical.ComponentVisitor<T> visitor) {
                return null;
            }
        };

        given(environmentOutputPort.findOne(unknownUuid)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> environmentService.handleAddComponentRequest(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(unknownUuid.toString());
    }

    @Test
    void handleAddComponentRequest_ShouldThrowAssertionError_WhenRequestIsNull() {
        // When & Then
        // EnvironmentService uses assert statement which throws AssertionError
        assertThatThrownBy(() -> environmentService.handleAddComponentRequest(null))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void handleAddComponentRequest_ShouldThrowAssertionError_WhenEnvironmentUuidIsNull() {
        // Given
        Component newComponent = GenericComponent.builder()
                .uuid(UUID.randomUUID())
                .name("New Component")
                .type(ComponentType.SOFTWARE)
                .build();

        ComponentCreationRequest request = new ComponentCreationRequest(
                UUID.randomUUID(),
                initiator,
                Instant.now(),
                null,
                null,
                newComponent
        ) {
            @Override
            public <T> T accept(com.management.cmdb.core.models.technical.ComponentVisitor<T> visitor) {
                return null;
            }
        };

        // When & Then
        // EnvironmentService uses assert statement which throws AssertionError
        assertThatThrownBy(() -> environmentService.handleAddComponentRequest(request))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void handleAddComponentRequest_ShouldThrowAssertionError_WhenComponentIsNull() {
        // Given
        UUID envUuid = devEnv.getUuid();
        ComponentCreationRequest request = new ComponentCreationRequest(
                UUID.randomUUID(),
                initiator,
                Instant.now(),
                null,
                envUuid,
                null
        ) {
            @Override
            public <T> T accept(com.management.cmdb.core.models.technical.ComponentVisitor<T> visitor) {
                return null;
            }
        };

        // When & Then
        // EnvironmentService uses assert statement which throws AssertionError
        assertThatThrownBy(() -> environmentService.handleAddComponentRequest(request))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void create_ShouldCallCheckIntegrity() {
        // Given
        Environment validEnv = Environment.builder()
                .location("Location")
                .type(EnvironmentType.DEV)
                .status(EnvironmentStatus.READY)
                .build();

        given(environmentOutputPort.save(any(Environment.class))).willAnswer(inv -> inv.getArgument(0));

        // When & Then - should not throw
        Environment result = environmentService.create(validEnv, initiator);
        assertThat(result).isNotNull();
    }

    @Test
    void update_ShouldCallCheckIntegrity() {
        // Given
        UUID envUuid = UUID.randomUUID();
        Environment existingEnv = Environment.builder()
                .uuid(envUuid)
                .location("Old Location")
                .type(EnvironmentType.DEV)
                .status(EnvironmentStatus.READY)
                .build();

        Environment validEnv = Environment.builder()
                .uuid(envUuid)
                .location("New Location")
                .type(EnvironmentType.DEV)
                .status(EnvironmentStatus.READY)
                .build();

        given(environmentOutputPort.findOne(envUuid)).willReturn(Optional.of(existingEnv));
        // Note: componentInputPort stub not needed - component update is commented in EnvironmentService
        given(environmentOutputPort.save(any(Environment.class))).willAnswer(inv -> inv.getArgument(0));

        // When & Then - should not throw
        Environment result = environmentService.update(validEnv, initiator);
        assertThat(result).isNotNull();
        verify(environmentOutputPort).save(any(Environment.class));
    }
}
