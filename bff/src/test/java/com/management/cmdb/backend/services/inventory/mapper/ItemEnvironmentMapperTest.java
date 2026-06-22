package com.management.cmdb.backend.services.inventory.mapper;

import com.management.cmdb.backend.services.inventory.dto.*;
import com.management.cmdb.core.models.business.component.GenericComponent;
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.business.project.Environment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemEnvironmentMapperTest {

    private final ItemEnvironmentMapper mapper = new ItemEnvironmentMapper();
    private final LocalDateTime now = LocalDateTime.of(2024, 1, 15, 10, 0);
    private final UUID environmentUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID component1Uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private final UUID component2Uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");

    @Test
    void shouldConvertItemDtoToCoreEnvironment() {
        // Arrange
        Set<AttributeDto> attributes = Set.of(
                AttributeDto.builder().label("Status").value("DEPLOYED").build(),
                AttributeDto.builder().label("Type").value("PROD").build(),
                AttributeDto.builder().label("Location").value("Paris DC").build(),
                AttributeDto.builder().label("JiraTracker").value("PROJ-123").build(),
                AttributeDto.builder().label("Revision").value("42").build()
        );

        Set<LinkDto> outgoingLinks = Set.of(
                new LinkDto(new LinkTypeDto("Compose of"), environmentUuid, component1Uuid, null),
                new LinkDto(new LinkTypeDto("Compose of"), environmentUuid, component2Uuid, null)
        );

        ItemDto itemDto = new ItemDto(
                environmentUuid,
                "Production Environment",
                "Main production environment",
                new ItemTypeDto("Environment"),
                attributes,
                outgoingLinks,
                Set.of(),
                now,
                null,
                null,
                null
        );

        // Act
        Environment result = mapper.mapItemDtoToEnvironment(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(environmentUuid, result.getUuid());
        assertEquals("Production Environment", result.getName());
        assertEquals("Main production environment", result.getDescription());
        assertEquals(EnvironmentStatus.DEPLOYED, result.getStatus());
        assertEquals(EnvironmentType.PROD, result.getType());
        assertEquals("Paris DC", result.getLocation());
        assertEquals("PROJ-123", result.getJiraTracker());
        assertEquals(42L, result.getRevision());
        assertEquals(now, result.getCreationDatetime());
        assertEquals(2, result.getComponents().size());

        // Verify components
        boolean hasComponent1 = result.getComponents().stream()
                .anyMatch(c -> c.getUuid().equals(component1Uuid));
        boolean hasComponent2 = result.getComponents().stream()
                .anyMatch(c -> c.getUuid().equals(component2Uuid));
        assertTrue(hasComponent1);
        assertTrue(hasComponent2);
    }

    @Test
    void shouldConvertItemDtoToCoreEnvironmentWithNullOptionalFields() {
        // Arrange
        Set<AttributeDto> attributes = Set.of(
                AttributeDto.builder().label("Location").value("Lyon DC").build()
        );

        ItemDto itemDto = new ItemDto(
                environmentUuid,
                "Test Environment",
                "Test env",
                new ItemTypeDto("Environment"),
                attributes,
                Set.of(),
                Set.of(),
                now,
                null,
                null,
                null
        );

        // Act
        Environment result = mapper.mapItemDtoToEnvironment(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(environmentUuid, result.getUuid());
        assertEquals("Test Environment", result.getName());
        assertEquals("Test env", result.getDescription());
        assertNull(result.getStatus());
        assertNull(result.getType());
        assertEquals("Lyon DC", result.getLocation());
        assertNull(result.getJiraTracker());
        assertEquals(0L, result.getRevision());
        assertTrue(result.getComponents().isEmpty());
    }

    @Test
    void shouldConvertCoreEnvironmentToItemDto() {
        // Arrange
        GenericComponent component1 = GenericComponent.builder()
                .uuid(component1Uuid)
                .build();
        GenericComponent component2 = GenericComponent.builder()
                .uuid(component2Uuid)
                .build();

        Environment environment = Environment.builder()
                .uuid(environmentUuid)
                .name("Production Environment")
                .description("Main production environment")
                .type(EnvironmentType.PROD)
                .status(EnvironmentStatus.DEPLOYED)
                .location("Paris DC")
                .jiraTracker("PROJ-123")
                .revision(42L)
                .creationDatetime(now)
                .components(Set.of(component1, component2))
                .build();

        // Act
        ItemDto result = mapper.mapEnvironmentToItemDto(environment);

        // Assert
        assertNotNull(result);
        assertEquals(environmentUuid, result.uuid());
        assertEquals("Production Environment", result.name());
        assertEquals("Main production environment", result.description());
        assertEquals("Environment", result.type().label());
        assertEquals(now, result.createdDate());

        // Verify attributes
        assertEquals(5, result.attributes().size());
        assertTrue(result.getMapAttributes().containsKey("Type"));
        assertEquals("PROD", result.getMapAttributes().get("Type"));
        assertTrue(result.getMapAttributes().containsKey("Revision"));
        assertEquals("42", result.getMapAttributes().get("Revision"));
        assertTrue(result.getMapAttributes().containsKey("JiraTracker"));
        assertEquals("PROJ-123", result.getMapAttributes().get("JiraTracker"));
        assertTrue(result.getMapAttributes().containsKey("Location"));
        assertEquals("Paris DC", result.getMapAttributes().get("Location"));
        assertTrue(result.getMapAttributes().containsKey("Status"));
        assertEquals("DEPLOYED", result.getMapAttributes().get("Status"));

        // Verify outgoing links
        assertEquals(2, result.outgoingLinks().size());
        boolean hasLinkToComponent1 = result.outgoingLinks().stream()
                .anyMatch(link -> link.targetItemId().equals(component1Uuid)
                        && link.linkType().label().equals("Compose of"));
        boolean hasLinkToComponent2 = result.outgoingLinks().stream()
                .anyMatch(link -> link.targetItemId().equals(component2Uuid)
                        && link.linkType().label().equals("Compose of"));
        assertTrue(hasLinkToComponent1);
        assertTrue(hasLinkToComponent2);
    }

    @Test
    void shouldConvertCoreEnvironmentToItemDtoWithNullOptionalFields() {
        // Arrange
        Environment environment = Environment.builder()
                .uuid(environmentUuid)
                .name("Minimal Environment")
                .description("Minimal env")
                .type(EnvironmentType.DEV)
                .status(EnvironmentStatus.REQUESTED)
                .location("Local")
                .creationDatetime(now)
                .components(Set.of())
                .build();

        // Act
        ItemDto result = mapper.mapEnvironmentToItemDto(environment);

        // Assert
        assertNotNull(result);
        assertEquals(environmentUuid, result.uuid());
        assertEquals("Minimal Environment", result.name());
        assertEquals("Minimal env", result.description());
        assertEquals("Environment", result.type().label());

        // Verify attributes with null/empty values
        assertEquals(5, result.attributes().size());
        assertEquals("DEV", result.getMapAttributes().get("Type"));
        assertEquals("0", result.getMapAttributes().get("Revision"));
        assertNull(result.getMapAttributes().get("JiraTracker"));
        assertEquals("Local", result.getMapAttributes().get("Location"));
        assertEquals("REQUESTED", result.getMapAttributes().get("Status"));
        assertTrue(result.outgoingLinks().isEmpty());
    }

    @Test
    void shouldPerformRoundTripConversionFromItemDto() {
        // Arrange
        Set<AttributeDto> attributes = Set.of(
                AttributeDto.builder().label("Status").value("READY").build(),
                AttributeDto.builder().label("Type").value("TEST").build(),
                AttributeDto.builder().label("Location").value("Test DC").build(),
                AttributeDto.builder().label("JiraTracker").value("TEST-456").build(),
                AttributeDto.builder().label("Revision").value("5").build()
        );

        Set<LinkDto> outgoingLinks = Set.of(
                new LinkDto(new LinkTypeDto("Compose of"), environmentUuid, component1Uuid, null)
        );

        ItemDto originalItemDto = new ItemDto(
                environmentUuid,
                "Test Environment",
                "Test description",
                new ItemTypeDto("Environment"),
                attributes,
                outgoingLinks,
                Set.of(),
                now,
                null,
                null,
                null
        );

        // Act: ItemDto -> Environment -> ItemDto
        Environment environment = mapper.mapItemDtoToEnvironment(originalItemDto);
        ItemDto roundTripItemDto = mapper.mapEnvironmentToItemDto(environment);

        // Assert
        assertEquals(originalItemDto.uuid(), roundTripItemDto.uuid());
        assertEquals(originalItemDto.name(), roundTripItemDto.name());
        assertEquals(originalItemDto.description(), roundTripItemDto.description());
        assertEquals(originalItemDto.createdDate(), roundTripItemDto.createdDate());
        assertEquals("Environment", roundTripItemDto.type().label());

        // Attributes
        assertEquals("READY", roundTripItemDto.getMapAttributes().get("Status"));
        assertEquals("TEST", roundTripItemDto.getMapAttributes().get("Type"));
        assertEquals("Test DC", roundTripItemDto.getMapAttributes().get("Location"));
        assertEquals("TEST-456", roundTripItemDto.getMapAttributes().get("JiraTracker"));
        assertEquals("5", roundTripItemDto.getMapAttributes().get("Revision"));

        // Links
        assertEquals(1, roundTripItemDto.outgoingLinks().size());
        assertTrue(roundTripItemDto.outgoingLinks().stream()
                .anyMatch(link -> link.targetItemId().equals(component1Uuid)));
    }

    @Test
    void shouldPerformRoundTripConversionFromEnvironment() {
        // Arrange
        GenericComponent component1 = GenericComponent.builder()
                .uuid(component1Uuid)
                .build();

        Environment originalEnvironment = Environment.builder()
                .uuid(environmentUuid)
                .name("Staging Environment")
                .description("Staging desc")
                .type(EnvironmentType.PRE_PROD)
                .status(EnvironmentStatus.IN_PROGRESS)
                .location("Staging DC")
                .jiraTracker("STAG-789")
                .revision(10L)
                .creationDatetime(now)
                .components(Set.of(component1))
                .build();

        // Act: Environment -> ItemDto -> Environment
        ItemDto itemDto = mapper.mapEnvironmentToItemDto(originalEnvironment);
        Environment roundTripEnvironment = mapper.mapItemDtoToEnvironment(itemDto);

        // Assert
        assertEquals(originalEnvironment.getUuid(), roundTripEnvironment.getUuid());
        assertEquals(originalEnvironment.getName(), roundTripEnvironment.getName());
        assertEquals(originalEnvironment.getDescription(), roundTripEnvironment.getDescription());
        assertEquals(originalEnvironment.getType(), roundTripEnvironment.getType());
        assertEquals(originalEnvironment.getStatus(), roundTripEnvironment.getStatus());
        assertEquals(originalEnvironment.getLocation(), roundTripEnvironment.getLocation());
        assertEquals(originalEnvironment.getJiraTracker(), roundTripEnvironment.getJiraTracker());
        assertEquals(originalEnvironment.getRevision(), roundTripEnvironment.getRevision());
        assertEquals(originalEnvironment.getCreationDatetime(), roundTripEnvironment.getCreationDatetime());
        assertEquals(1, roundTripEnvironment.getComponents().size());
        assertTrue(roundTripEnvironment.getComponents().stream()
                .anyMatch(c -> c.getUuid().equals(component1Uuid)));
    }

    @Test
    void shouldFilterOutNonComponentLinks() {
        // Arrange
        Set<AttributeDto> attributes = Set.of(
                AttributeDto.builder().label("Status").value("DEPLOYED").build(),
                AttributeDto.builder().label("Type").value("PROD").build(),
                AttributeDto.builder().label("Location").value("Paris DC").build()
        );

        UUID otherItemUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
        Set<LinkDto> outgoingLinks = Set.of(
                new LinkDto(new LinkTypeDto("Compose of"), environmentUuid, component1Uuid, null),
                new LinkDto(new LinkTypeDto("Depends on"), environmentUuid, otherItemUuid, null)
        );

        ItemDto itemDto = new ItemDto(
                environmentUuid,
                "Production Environment",
                "Main production environment",
                new ItemTypeDto("Environment"),
                attributes,
                outgoingLinks,
                Set.of(),
                now,
                null,
                null,
                null
        );

        // Act
        Environment result = mapper.mapItemDtoToEnvironment(itemDto);

        // Assert - only "Compose of" links should be converted to components
        assertEquals(1, result.getComponents().size());
        assertTrue(result.getComponents().stream()
                .anyMatch(c -> c.getUuid().equals(component1Uuid)));
        assertFalse(result.getComponents().stream()
                .anyMatch(c -> c.getUuid().equals(otherItemUuid)));
    }
}