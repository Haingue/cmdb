package com.management.cmdb.backend.endpoint.mapper;

import com.management.cmdb.backend.endpoint.environment.dto.EnvironmentDto;
import com.management.cmdb.backend.endpoint.environment.mapper.EnvironmentMapper;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.technical.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentMapperTest {

    @Test
    void shouldConvertEnvironmentToDto() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Set<Component> components = new HashSet<>();
        List<Event> events = List.of(new Event(null, null, "test-user"));
        LocalDateTime now = LocalDateTime.now();

        Environment environment = Environment.builder()
                .uuid(uuid)
                .name("Test Environment")
                .description("Test Description")
                .location("Test Location")
                .type(EnvironmentType.PROD)
                .components(components)
                .jiraTracker("TEST-123")
                .status(EnvironmentStatus.DEPLOYED)
                .revision(1L)
                .events(events)
                .creationDatetime(now)
                .archiveDatetime(null)
                .build();

        // Act
        EnvironmentDto dto = EnvironmentMapper.INSTANCE.toDto(environment);

        // Assert
        assertNotNull(dto);
        assertEquals(uuid, dto.uuid());
        assertEquals("Test Environment", dto.name());
        assertEquals("Test Description", dto.description());
        assertEquals("Test Location", dto.location());
        assertEquals(EnvironmentType.PROD, dto.type());
        assertEquals(components, dto.components());
        assertEquals("TEST-123", dto.jiraTracker());
        assertEquals(EnvironmentStatus.DEPLOYED, dto.status());
        assertEquals(1L, dto.revision());
        assertEquals(events, dto.events());
        assertEquals(now, dto.creationDatetime());
        assertNull(dto.archiveDatetime());
    }

    @Test
    void shouldConvertDtoToEnvironment() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Set<Component> components = new HashSet<>();
        List<Event> events = List.of(new Event(null, null, "test-user"));
        LocalDateTime now = LocalDateTime.now();

        EnvironmentDto dto = new EnvironmentDto(
                uuid,
                "Test Environment",
                "Test Description",
                "Test Location",
                EnvironmentType.PROD,
                components,
                "TEST-123",
                EnvironmentStatus.DEPLOYED,
                1L,
                events,
                now,
                null
        );

        // Act
        Environment environment = EnvironmentMapper.INSTANCE.toCoreModel(dto);

        // Assert
        assertNotNull(environment);
        assertEquals(uuid, environment.getUuid());
        assertEquals("Test Environment", environment.getName());
        assertEquals("Test Description", environment.getDescription());
        assertEquals("Test Location", environment.getLocation());
        assertEquals(EnvironmentType.PROD, environment.getType());
        assertEquals(components, environment.getComponents());
        assertEquals("TEST-123", environment.getJiraTracker());
        assertEquals(EnvironmentStatus.DEPLOYED, environment.getStatus());
        assertEquals(1L, environment.getRevision());
        assertEquals(events, environment.getEvents());
        assertEquals(now, environment.getCreationDatetime());
        assertNull(environment.getArchiveDatetime());
    }

    @Test
    void shouldHandleNullValues() {
        // Act & Assert
        assertNull(EnvironmentMapper.INSTANCE.toDto(null));
        assertNull(EnvironmentMapper.INSTANCE.toCoreModel(null));
    }
}
