package com.management.cmdb.backend.services.inventory.mapper.component;

import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.backend.services.inventory.dto.LinkDto;
import com.management.cmdb.backend.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.component.Software;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.TechnologyType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemSoftwareMapperTest {

    private final ItemSoftwareMapper mapper = ItemSoftwareMapper.INSTANCE;

    @Test
    void shouldMapItemDtoToSoftwareWithAllAttributes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Test Software";
        String description = "Production Software";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("Cert123").build());
        attributes.add(AttributeDto.builder().label("Version").value("1.2.3").build());
        attributes.add(AttributeDto.builder().label("Technology").value("Java").build());
        attributes.add(AttributeDto.builder().label("SoftwareType").value("BACKEND").build());
        attributes.add(AttributeDto.builder().label("RepositoryUrl").value("https://github.com/test/repo").build());

        // Add host links
        Set<LinkDto> outgoingLinks = new HashSet<>();
        UUID host1Uuid = UUID.randomUUID();
        UUID host2Uuid = UUID.randomUUID();
        outgoingLinks.add(new LinkDto(new LinkTypeDto("Hosted on"), uuid, host1Uuid, null));
        outgoingLinks.add(new LinkDto(new LinkTypeDto("Hosted on"), uuid, host2Uuid, null));

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("SOFTWARE"),
                attributes,
                outgoingLinks,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Software result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ComponentType.SOFTWARE, result.getType());
        assertEquals("Cert123", result.getCertificate());
        assertEquals(Version.fromString("1.2.3"), result.getVersion());
        assertEquals("Java", result.getTechnology().getName());
        assertEquals(TechnologyType.BACKEND, result.getSoftwareType());
        assertEquals(URI.create("https://github.com/test/repo"), result.getRepositoryUrl());
        assertEquals(createdDate, result.getCreationDatetime());
        
        // Check hosts
        assertNotNull(result.getHosts());
        assertEquals(2, result.getHosts().size());
        Set<UUID> hostUuids = result.getHosts().stream()
                .map(Host::getUuid)
                .collect(java.util.stream.Collectors.toSet());
        assertTrue(hostUuids.contains(host1Uuid));
        assertTrue(hostUuids.contains(host2Uuid));
    }

    @Test
    void shouldMapItemDtoToSoftwareWithMinimalAttributes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Minimal Software";
        String description = "Minimal Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        // Only required attributes
        attributes.add(AttributeDto.builder().label("Certificate").value("MinCert").build());

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("SOFTWARE"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Software result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ComponentType.SOFTWARE, result.getType());
        assertEquals("MinCert", result.getCertificate());
        assertNull(result.getVersion());
        assertNull(result.getTechnology());
        assertNull(result.getSoftwareType());
        assertNull(result.getRepositoryUrl());
        assertNotNull(result.getHosts());
        assertTrue(result.getHosts().isEmpty());
    }

    @Test
    void shouldMapSoftwareToItemDtoWithAllAttributes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Test Software";
        String description = "Production Software";
        LocalDateTime createdDate = LocalDateTime.now();
        
        // Create hosts
        UUID host1Uuid = UUID.randomUUID();
        UUID host2Uuid = UUID.randomUUID();
        Set<Host> hosts = Set.of(
                Host.builder().uuid(host1Uuid).build(),
                Host.builder().uuid(host2Uuid).build()
        );
        
        Software software = Software.builder()
                .uuid(uuid)
                .name(name)
                .description(description)
                .type(ComponentType.SOFTWARE)
                .certificate("Cert123")
                .version(Version.fromString("1.2.3"))
                .technology(Technology.builder().name("Java").build())
                .softwareType(TechnologyType.BACKEND)
                .repositoryUrl(URI.create("https://github.com/test/repo"))
                .hosts(hosts)
                .creationDatetime(createdDate)
                .build();

        // Act
        ItemDto result = mapper.mapToItemDto(software);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.uuid());
        assertEquals(name, result.name());
        assertEquals(description, result.description());
        assertEquals("SOFTWARE", result.type().label());
        assertEquals(5, result.attributes().size()); // All attributes should be present
        
        // Verify specific attributes
        assertTrue(result.getMapAttributes().containsKey("Certificate"));
        assertEquals("Cert123", result.getMapAttributes().get("Certificate"));
        
        assertTrue(result.getMapAttributes().containsKey("Version"));
        assertEquals("1.2.3", result.getMapAttributes().get("Version"));
        
        assertTrue(result.getMapAttributes().containsKey("Technology"));
        assertEquals("Java", result.getMapAttributes().get("Technology"));
        
        assertTrue(result.getMapAttributes().containsKey("SoftwareType"));
        assertEquals("BACKEND", result.getMapAttributes().get("SoftwareType"));
        
        assertTrue(result.getMapAttributes().containsKey("RepositoryUrl"));
        assertEquals("https://github.com/test/repo", result.getMapAttributes().get("RepositoryUrl"));
        
        // Verify outgoing links (hosts)
        assertNotNull(result.outgoingLinks());
        assertEquals(2, result.outgoingLinks().size());
        
        Set<UUID> resultHostUuids = result.outgoingLinks().stream()
                .filter(link -> "Hosted on".equals(link.linkType().label()))
                .map(LinkDto::targetItemId)
                .collect(java.util.stream.Collectors.toSet());
        
        assertTrue(resultHostUuids.contains(host1Uuid));
        assertTrue(resultHostUuids.contains(host2Uuid));
    }

    @Test
    void shouldMapSoftwareToItemDtoWithNullAttributes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Minimal Software";
        String description = "Minimal Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Software software = Software.builder()
                .uuid(uuid)
                .name(name)
                .description(description)
                .type(ComponentType.SOFTWARE)
                .certificate(null)
                .version(null)
                .technology(null)
                .softwareType(null)
                .repositoryUrl(null)
                .hosts(Set.of())
                .creationDatetime(createdDate)
                .build();

        // Act
        ItemDto result = mapper.mapToItemDto(software);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.uuid());
        assertEquals(name, result.name());
        assertEquals(description, result.description());
        assertEquals("SOFTWARE", result.type().label());
        // Should have empty attributes since all values are null
        assertTrue(result.attributes().isEmpty());
        // Should have empty outgoing links
        assertNotNull(result.outgoingLinks());
        assertTrue(result.outgoingLinks().isEmpty());
    }

    @Test
    void shouldHandleRoundTripConversion() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Round Trip Software";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("RoundCert").build());
        attributes.add(AttributeDto.builder().label("Version").value("2.3.4").build());
        attributes.add(AttributeDto.builder().label("Technology").value("Round Tech").build());
        attributes.add(AttributeDto.builder().label("SoftwareType").value("FRONTEND").build());
        attributes.add(AttributeDto.builder().label("RepositoryUrl").value("https://github.com/round/repo").build());

        // Add host link
        Set<LinkDto> outgoingLinks = new HashSet<>();
        UUID hostUuid = UUID.randomUUID();
        outgoingLinks.add(new LinkDto(new LinkTypeDto("Hosted on"), uuid, hostUuid, null));

        ItemDto originalItemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("SOFTWARE"),
                attributes,
                outgoingLinks,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act - Convert to Software and back to ItemDto
        Software software = mapper.mapToCoreModel(originalItemDto);
        ItemDto resultItemDto = mapper.mapToItemDto(software);

        // Assert
        assertNotNull(resultItemDto);
        assertEquals(originalItemDto.uuid(), resultItemDto.uuid());
        assertEquals(originalItemDto.name(), resultItemDto.name());
        assertEquals(originalItemDto.description(), resultItemDto.description());
        assertEquals(originalItemDto.type().label(), resultItemDto.type().label());
        
        // Check that attributes are preserved
        assertEquals(5, resultItemDto.attributes().size());
        assertTrue(resultItemDto.getMapAttributes().containsKey("Certificate"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("Version"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("Technology"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("SoftwareType"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("RepositoryUrl"));
        
        // Check that host link is preserved
        assertNotNull(resultItemDto.outgoingLinks());
        assertEquals(1, resultItemDto.outgoingLinks().size());
        assertTrue(resultItemDto.outgoingLinks().stream()
                .anyMatch(link -> "Hosted on".equals(link.linkType().label()) && hostUuid.equals(link.targetItemId())));
    }

    @Test
    void shouldHandleDifferentSoftwareTypes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Database Software";
        String description = "Test Database Software";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("DBCert").build());
        attributes.add(AttributeDto.builder().label("SoftwareType").value("DATABASE").build());

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("SOFTWARE"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Software result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ComponentType.SOFTWARE, result.getType());
        assertEquals(TechnologyType.DATABASE, result.getSoftwareType());
    }

    @Test
    void shouldHandleInvalidRepositoryUrl() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Invalid URL Software";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("TestCert").build());
        attributes.add(AttributeDto.builder().label("RepositoryUrl").value("invalid-url").build());

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("SOFTWARE"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act & Assert - should handle invalid URL gracefully
        Software result = mapper.mapToCoreModel(itemDto);
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        // Repository URL should be set (URI.create is lenient)
        assertNotNull(result.getRepositoryUrl());
    }

    @Test
    void shouldHandleSoftwareWithNoHosts() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Standalone Software";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("StandaloneCert").build());

        // No outgoing links
        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("SOFTWARE"),
                attributes,
                null, // No outgoing links
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Software result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        assertNotNull(result.getHosts());
        assertTrue(result.getHosts().isEmpty());
    }

    @Test
    void shouldHandleSoftwareWithMultipleHosts() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Multi-Host Software";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("MultiCert").build());

        // Add multiple host links
        Set<LinkDto> outgoingLinks = new HashSet<>();
        UUID host1Uuid = UUID.randomUUID();
        UUID host2Uuid = UUID.randomUUID();
        UUID host3Uuid = UUID.randomUUID();
        outgoingLinks.add(new LinkDto(new LinkTypeDto("Hosted on"), uuid, host1Uuid, null));
        outgoingLinks.add(new LinkDto(new LinkTypeDto("Hosted on"), uuid, host2Uuid, null));
        outgoingLinks.add(new LinkDto(new LinkTypeDto("Hosted on"), uuid, host3Uuid, null));

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("SOFTWARE"),
                attributes,
                outgoingLinks,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Software result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        assertNotNull(result.getHosts());
        assertEquals(3, result.getHosts().size());
        
        Set<UUID> hostUuids = result.getHosts().stream()
                .map(Host::getUuid)
                .collect(java.util.stream.Collectors.toSet());
        assertTrue(hostUuids.contains(host1Uuid));
        assertTrue(hostUuids.contains(host2Uuid));
        assertTrue(hostUuids.contains(host3Uuid));
    }

    @Test
    void shouldHandleDifferentComponentTypes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Flexible Software";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("FlexCert").build());

        // Test with different type labels
        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("IOT"), // Different type
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Software result = mapper.mapToCoreModel(itemDto);

        // Assert - should default to HOST if type is not SOFTWARE
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        // The type should be set to the value from DTO or default
        assertEquals("FlexCert", result.getCertificate());
    }

    @Test
    void shouldIgnoreNonHostLinks() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Software with Mixed Links";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("TestCert").build());

        // Add different types of links
        Set<LinkDto> outgoingLinks = new HashSet<>();
        UUID hostUuid = UUID.randomUUID();
        UUID otherUuid = UUID.randomUUID();
        outgoingLinks.add(new LinkDto(new LinkTypeDto("Hosted on"), uuid, hostUuid, null)); // Should be included
        outgoingLinks.add(new LinkDto(new LinkTypeDto("Depends on"), uuid, otherUuid, null)); // Should be ignored

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("SOFTWARE"),
                attributes,
                outgoingLinks,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Software result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertNotNull(result.getHosts());
        assertEquals(1, result.getHosts().size()); // Only the "Hosted on" link should be included
        assertEquals(hostUuid, result.getHosts().iterator().next().getUuid());
    }
}