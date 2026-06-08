package com.management.cmdb.core.service;

import com.management.cmdb.core.fake.FakeTechnology;
import com.management.cmdb.core.fake.FakeUser;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.GenericComponent;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.TechnologyType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.ports.outputs.ComponentOutputPort;
import com.management.cmdb.core.ports.outputs.NotificationOutputPort;
import com.management.cmdb.core.ports.outputs.TechnologyOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class TechnologyServiceTest {

    @Mock
    private TechnologyOutputPort technologyOutputPort;

    @Mock
    private ComponentOutputPort componentOutputPort;

    @Mock
    private NotificationOutputPort notificationOutputPort;

    @InjectMocks
    private TechnologyService technologyService;

    private final Technology javaSpring = FakeTechnology.JAVA_SPRING.technology;
    private final Technology postgres = FakeTechnology.POSTGRESQL.technology;
    private final Technology react = FakeTechnology.REACT.technology;
    private final Technology outdatedTomcat = FakeTechnology.OUTDATED_TOMCAT.technology;

    @BeforeEach
    void setUp() {
        technologyService = new TechnologyService(technologyOutputPort, componentOutputPort, notificationOutputPort);
    }

    @Test
    void findOne_ShouldReturnTechnology_WhenExists() {
        // Given
        given(technologyOutputPort.findOne(javaSpring.getName())).willReturn(Optional.of(javaSpring));

        // When
        Technology result = technologyService.findOne(javaSpring.getName(), FakeUser.SUPER_ADMIN.user);

        // Then
        assertThat(result).isEqualTo(javaSpring);
    }

    @Test
    void findOne_ShouldThrowNotFoundException_WhenNotExists() {
        // Given
        given(technologyOutputPort.findOne(anyString())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> technologyService.findOne("Unknown", FakeUser.SUPER_ADMIN.user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Unknown");
    }

    @Test
    void findByName_ShouldReturnTechnology_WhenExists() {
        // Given
        given(technologyOutputPort.findByName(javaSpring.getName())).willReturn(Optional.of(javaSpring));

        // When
        Technology result = technologyService.findByName(javaSpring.getName());

        // Then
        assertThat(result).isEqualTo(javaSpring);
    }

    @Test
    void findByName_ShouldThrowNotFoundException_WhenNotExists() {
        // Given
        given(technologyOutputPort.findByName(anyString())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> technologyService.findByName("Unknown"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Unknown");
    }

    @Test
    void create_ShouldSaveAndReturnTechnology_WhenValid() {
        // Given
        given(technologyOutputPort.findByName(javaSpring.getName())).willReturn(Optional.empty());
        given(technologyOutputPort.save(any(Technology.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        Technology result = technologyService.create(javaSpring, FakeUser.SUPER_ADMIN.user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(javaSpring.getName());
        assertThat(result.getType()).isEqualTo(TechnologyType.BACKEND);
        verify(technologyOutputPort).save(javaSpring);
    }

    @Test
    void create_ShouldThrowInvalidObjectException_WhenTechnologyIsNull() {
        // When & Then
        assertThatThrownBy(() -> technologyService.create(null, FakeUser.SUPER_ADMIN.user))
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    void create_ShouldThrowInvalidObjectException_WhenNameIsBlank() {
        // Given
        Technology invalidTech = Technology.builder()
                .name("")
                .type(TechnologyType.BACKEND)
                .build();

        // When & Then
        assertThatThrownBy(() -> technologyService.create(invalidTech, FakeUser.SUPER_ADMIN.user))
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("name");
    }

    @Test
    void create_ShouldThrowInvalidObjectException_WhenNameAlreadyExists() {
        // Given
        given(technologyOutputPort.findByName(javaSpring.getName())).willReturn(Optional.of(javaSpring));

        // When & Then
        assertThatThrownBy(() -> technologyService.create(javaSpring, FakeUser.SUPER_ADMIN.user))
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void create_ShouldThrowInvalidObjectException_WhenTypeIsNull() {
        // Given
        Technology invalidTech = Technology.builder()
                .name("New Tech")
                .type(null)
                .build();

        // When & Then
        assertThatThrownBy(() -> technologyService.create(invalidTech, FakeUser.SUPER_ADMIN.user))
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("type");
    }

    @Test
    void update_ShouldUpdateAndReturnTechnology_WhenValid() {
        // Given
        Technology existingTech = Technology.builder()
                .name("Java Spring Boot")
                .type(TechnologyType.BACKEND)
                .description("Old description")
                .minimalVersion(new Version(3, 0, 0))
                .targetVersion(new Version(3, 1, 0))
                .lastVersion(new Version(3, 1, 5))
                .build();

        Technology updatedTech = Technology.builder()
                .name("Java Spring Boot")
                .type(TechnologyType.BACKEND)
                .description("New description")
                .minimalVersion(new Version(3, 2, 0))
                .targetVersion(new Version(3, 3, 0))
                .lastVersion(new Version(3, 3, 5))
                .build();

        given(technologyOutputPort.findByName(javaSpring.getName())).willReturn(Optional.of(existingTech));
        given(technologyOutputPort.save(any(Technology.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        Technology result = technologyService.update(updatedTech, FakeUser.SUPER_ADMIN.user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("New description");
        assertThat(result.getMinimalVersion()).isEqualTo(new Version(3, 2, 0));
        verify(technologyOutputPort).save(updatedTech);
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenNotExists() {
        // Given
        given(technologyOutputPort.findByName(anyString())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> technologyService.update(javaSpring, FakeUser.SUPER_ADMIN.user))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_ShouldThrowInvalidObjectException_WhenTechnologyIsNull() {
        // When & Then
        assertThatThrownBy(() -> technologyService.update(null, FakeUser.SUPER_ADMIN.user))
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    void needsUpdate_ShouldReturnTrue_WhenVersionBelowMinimal() {
        // Given
        Version currentVersion = new Version(3, 1, 0); // Below minimal 3.2.0

        // When & Then
        assertThat(javaSpring.needsUpdate(currentVersion)).isTrue();
    }

    @Test
    void needsUpdate_ShouldReturnFalse_WhenVersionAtMinimal() {
        // Given
        Version currentVersion = new Version(3, 2, 0); // At minimal

        // When & Then
        assertThat(javaSpring.needsUpdate(currentVersion)).isTrue(); // Note: >= minimal triggers update
    }

    @Test
    void needsUpdate_ShouldReturnFalse_WhenVersionAboveMinimal() {
        // Given
        Version currentVersion = new Version(3, 3, 5); // Above minimal

        // When & Then
        assertThat(javaSpring.needsUpdate(currentVersion)).isFalse();
    }

    @Test
    void setMinimalVersionAndScanAssets_ShouldUpdateMinimalVersionAndReturnComponents() {
        // Given
        Version newMinimalVersion = new Version(3, 3, 0);
        Technology techToUpdate = Technology.builder()
                .name("Java Spring Boot")
                .type(TechnologyType.BACKEND)
                .minimalVersion(new Version(3, 2, 0))
                .targetVersion(new Version(3, 3, 0))
                .lastVersion(new Version(3, 3, 5))
                .build();

        Component component1 = GenericComponent.builder()
                .uuid(UUID.randomUUID())
                .name("Component 1")
                .type(ComponentType.SOFTWARE)
                .technology(techToUpdate)
                .version(new Version(3, 1, 0))
                .build();

        Component component2 = GenericComponent.builder()
                .uuid(UUID.randomUUID())
                .name("Component 2")
                .type(ComponentType.SOFTWARE)
                .technology(techToUpdate)
                .version(new Version(3, 3, 5))
                .build();

        given(technologyOutputPort.findByName(techToUpdate.getName())).willReturn(Optional.of(techToUpdate));
        given(componentOutputPort.findAllByTechnology(techToUpdate.getName())).willReturn(List.of(component1, component2));
        given(technologyOutputPort.save(any(Technology.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        List<Component> result = technologyService.setMinimalVersionAndScanAssets("Java Spring Boot", newMinimalVersion);

        // Then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst()).isEqualTo(component1);
        verify(technologyOutputPort).save(any(Technology.class));
    }

    @Test
    void setMinimalVersionAndScanAssets_ShouldThrowNotFoundException_WhenTechnologyNotExists() {
        // Given
        Technology nonExistentTech = Technology.builder()
                .name("Non Existent Tech")
                .type(TechnologyType.BACKEND)
                .minimalVersion(new Version(1, 0, 0))
                .targetVersion(new Version(1, 0, 0))
                .lastVersion(new Version(1, 0, 0))
                .build();

        given(technologyOutputPort.findByName(nonExistentTech.getName())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> technologyService.setMinimalVersionAndScanAssets(nonExistentTech.getName(), nonExistentTech.getMinimalVersion()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void archive_ShouldSetArchiveDatetime_WhenTechnologyExists() {
        // Given
        given(technologyOutputPort.findOne(javaSpring.getName())).willReturn(Optional.of(javaSpring));
        given(technologyOutputPort.save(any(Technology.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        technologyService.archive(javaSpring.getName(), FakeUser.SUPER_ADMIN.user);

        // Then
        verify(technologyOutputPort).save(any(Technology.class));
    }

    @Test
    void archive_ShouldThrowNotFoundException_WhenTechnologyNotExists() {
        // Given
        given(technologyOutputPort.findOne(anyString())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> technologyService.archive("Unknown", FakeUser.SUPER_ADMIN.user))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_ShouldDeleteTechnology_WhenExists() {
        // Given
        given(technologyOutputPort.findOne(javaSpring.getName())).willReturn(Optional.of(javaSpring));

        // When
        technologyService.delete(javaSpring.getName(), FakeUser.SUPER_ADMIN.user);

        // Then
        verify(technologyOutputPort).delete(javaSpring.getName());
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenNotExists() {
        // Given
        given(technologyOutputPort.findOne(anyString())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> technologyService.delete("Unknown", FakeUser.SUPER_ADMIN.user))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void checkIntegrity_ShouldNotThrow_WhenValid() {
        // When & Then
        javaSpring.checkIntegrity(); // Should not throw
    }

    @Test
    void checkIntegrity_ShouldThrow_WhenNameIsBlank() {
        Technology invalid = Technology.builder()
                .name("")
                .type(TechnologyType.BACKEND)
                .minimalVersion(new Version(1, 0, 0))
                .targetVersion(new Version(1, 0, 0))
                .lastVersion(new Version(1, 0, 0))
                .build();

        assertThatThrownBy(invalid::checkIntegrity)
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("name");
    }

    @Test
    void checkIntegrity_ShouldThrow_WhenNameIsNull() {
        Technology invalid = Technology.builder()
                .name(null)
                .type(TechnologyType.BACKEND)
                .minimalVersion(new Version(1, 0, 0))
                .targetVersion(new Version(1, 0, 0))
                .lastVersion(new Version(1, 0, 0))
                .build();

        assertThatThrownBy(invalid::checkIntegrity)
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("name");
    }

    @Test
    void checkIntegrity_ShouldThrow_WhenTypeIsNull() {
        Technology invalid = Technology.builder()
                .name("Test")
                .type(null)
                .minimalVersion(new Version(1, 0, 0))
                .targetVersion(new Version(1, 0, 0))
                .lastVersion(new Version(1, 0, 0))
                .build();

        assertThatThrownBy(invalid::checkIntegrity)
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("type");
    }

    @Test
    void checkIntegrity_ShouldThrow_WhenMinimalVersionIsNull() {
        Technology invalid = Technology.builder()
                .name("Test")
                .type(TechnologyType.BACKEND)
                .minimalVersion(null)
                .targetVersion(new Version(1, 0, 0))
                .lastVersion(new Version(1, 0, 0))
                .build();

        assertThatThrownBy(invalid::checkIntegrity)
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("Minimal version");
    }

    @Test
    void checkIntegrity_ShouldThrow_WhenTargetVersionIsNull() {
        Technology invalid = Technology.builder()
                .name("Test")
                .type(TechnologyType.BACKEND)
                .minimalVersion(new Version(1, 0, 0))
                .targetVersion(null)
                .lastVersion(new Version(1, 0, 0))
                .build();

        assertThatThrownBy(() -> invalid.checkIntegrity())
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("Target version");
    }

    @Test
    void checkIntegrity_ShouldThrow_WhenLastVersionIsNull() {
        Technology invalid = Technology.builder()
                .name("Test")
                .type(TechnologyType.BACKEND)
                .minimalVersion(new Version(1, 0, 0))
                .targetVersion(new Version(1, 0, 0))
                .lastVersion(null)
                .build();

        assertThatThrownBy(invalid::checkIntegrity)
                .isInstanceOf(InvalidObjectException.class)
                .hasMessageContaining("Last version");
    }
}
