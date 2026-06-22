package com.management.cmdb.backend.services.inventory.mapper;

import com.management.cmdb.backend.services.inventory.deserializer.ProjectItemDeserializer;
import com.management.cmdb.backend.services.inventory.dto.*;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemProjectMapperTest {

    private final ItemProjectMapper mapper = new ItemProjectMapper();

    @Test
    void shouldConvertItemDtoToCoreProject() {
        // Arrange
        UUID projectUuid = UUID.randomUUID();
        UUID businessServiceUuid = UUID.randomUUID();
        UUID environmentUuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ItemDto itemDto = new ItemDto(
                projectUuid,
                "Full Project Name",
                "Test Description",
                new ItemTypeDto(null, "Project", null, null, null, null, null, null),
                Set.of(
                        new AttributeDto(null, "ShortName", null, "PPR", null, null, null, null)
                ),
                Set.of(
                        new LinkDto(new LinkTypeDto("Implements"), projectUuid, businessServiceUuid, null),
                        new LinkDto(new LinkTypeDto("Deployed in"), projectUuid, environmentUuid, null)
                ),
                Set.of(),
                now,
                null,
                null,
                null
        );

        // Act
        Project result = mapper.mapItemDtoToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(projectUuid, result.getUuid());
        assertEquals("Full Project Name", result.getFullName());
        assertEquals("PPR", result.getShortName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(now, result.getCreationDatetime());
        assertNotNull(result.getBusinessService());
        assertEquals(businessServiceUuid.toString(), result.getBusinessService().getName());
        assertNotNull(result.getEnvironments());
        assertEquals(1, result.getEnvironments().size());
        assertTrue(result.getEnvironments().stream()
                .anyMatch(env -> env.getUuid().equals(environmentUuid)));
    }

    @Test
    void shouldConvertCoreProjectToItemDto() {
        // Arrange
        UUID projectUuid = UUID.randomUUID();
        UUID environmentUuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        BusinessService businessService = BusinessService.builder()
                .name("Service Name")
                .abbreviation("SVC")
                .build();

        Environment environment = Environment.builder()
                .uuid(environmentUuid)
                .build();

        Project project = Project.builder()
                .uuid(projectUuid)
                .fullName("Full Project Name")
                .shortName("PPR")
                .description("Project Description")
                .businessService(businessService)
                .environments(Set.of(environment))
                .creationDatetime(now)
                .build();

        // Act
        ItemDto result = mapper.mapCoreModelToItemDto(project);

        // Assert
        assertNotNull(result);
        assertNull(result.uuid());
        assertEquals("Full Project Name", result.name());
        assertEquals("Project Description", result.description());
        assertNotNull(result.type());
        assertEquals("Project", result.type().label());
        assertNotNull(result.attributes());
        assertEquals(1, result.attributes().size());
        assertTrue(result.attributes().stream()
                .anyMatch(attr -> attr.getLabel().equals("ShortName") && attr.getValue().equals("PPR")));
        assertNotNull(result.outgoingLinks());
        assertEquals(project.getEnvironments().size(), result.outgoingLinks().size());
        assertTrue(result.outgoingLinks().stream()
                .anyMatch(link -> link.linkType().label().equals(ProjectItemDeserializer.EnvironmentLinkType) &&
                        link.sourceItemId().equals(projectUuid) &&
                        link.targetItemId().equals(environmentUuid)));
        assertEquals(now, result.createdDate());
    }

    @Test
    void shouldConvertItemDtoToCoreProjectWithNullAttributes() {
        // Arrange
        UUID projectUuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ItemDto itemDto = new ItemDto(
                projectUuid,
                "Test Project",
                "Test Description",
                new ItemTypeDto(null, "Project", null, null, null, null, null, null),
                Set.of(
                ),
                Set.of(),
                Set.of(),
                now,
                null,
                null,
                null
        );

        // Act
        Project result = mapper.mapItemDtoToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(projectUuid, result.getUuid());
        assertNotNull(result.getFullName());
        assertNull(result.getShortName());
        assertEquals("Test Description", result.getDescription());
        assertNull(result.getBusinessService());
        assertTrue(result.getEnvironments().isEmpty());
    }

    @Test
    void shouldConvertItemDtoToCoreProjectWithNoMatchingLinks() {
        // Arrange
        UUID projectUuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ItemDto itemDto = new ItemDto(
                projectUuid,
                "Full Name",
                "Test Description",
                new ItemTypeDto(null, "Project", null, null, null, null, null, null),
                Set.of(
                        new AttributeDto(null, "ShortName", null, "SHRT", null, null, null, null)
                ),
                Set.of(),
                Set.of(
                        new LinkDto(new LinkTypeDto("OtherType"), projectUuid, UUID.randomUUID(), null)
                ),
                now,
                null,
                null,
                null
        );

        // Act
        Project result = mapper.mapItemDtoToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals("Full Name", result.getFullName());
        assertEquals("SHRT", result.getShortName());
        assertNull(result.getBusinessService());
        assertTrue(result.getEnvironments().isEmpty());
    }
}