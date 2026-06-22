package com.management.cmdb.backend.services.inventory.mapper;

import com.management.cmdb.backend.services.inventory.dto.*;
import com.management.cmdb.core.models.business.component.GenericComponent;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.TechnologyType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemGenericComponentMapperTest {

    private final ItemGenericComponentMapper mapper = new ItemGenericComponentMapper();

    @Test
    void shouldConvertItemDtoToCoreGenericComponent() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Test Component";
        String description = "Test Description";
        ItemTypeDto type = new ItemTypeDto("SOFTWARE");
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder()
                .label("Certificate")
                .value("Cert123")
                .build());
        attributes.add(AttributeDto.builder()
                .label("Technology")
                .value("SpringBoot")
                .build());
        attributes.add(AttributeDto.builder()
                .label("Version")
                .value("1.2.3")
                .build());

        LocalDateTime createdDate = LocalDateTime.now();
        UUID createdBy = UUID.randomUUID();
        
        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                type,
                attributes,
                null,
                null,
                createdDate,
                createdBy,
                createdBy,
                createdDate
        );

        // Act
        GenericComponent result = mapper.mapItemDtoToGenericComponent(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ComponentType.SOFTWARE, result.getType());
        assertEquals(createdDate, result.getCreationDatetime());
        assertEquals("Cert123", result.getCertificate());
        assertEquals("SpringBoot", result.getTechnology().getName());
        assertEquals(Version.fromString("1.2.3"), result.getVersion());
    }

    @Test
    void shouldConvertItemDtoToCoreGenericComponentWithNullAttributes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Test Component";
        String description = "Test Description";
        ItemTypeDto type = new ItemTypeDto("HARDWARE");
        
        Set<AttributeDto> attributes = new HashSet<>();
        // No attributes added

        LocalDateTime createdDate = LocalDateTime.now();
        UUID createdBy = UUID.randomUUID();
        
        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                type,
                attributes,
                null,
                null,
                createdDate,
                createdBy,
                createdBy,
                createdDate
        );

        // Act
        GenericComponent result = mapper.mapItemDtoToGenericComponent(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ComponentType.HARDWARE, result.getType());
        assertEquals(createdDate, result.getCreationDatetime());
        assertNull(result.getCertificate());
        assertNull(result.getTechnology());
        assertNull(result.getVersion());
    }

    @Test
    void shouldConvertCoreGenericComponentToItemDto() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Test Component";
        String description = "Test Description";
        ComponentType type = ComponentType.SOFTWARE;
        String certificate = "Cert123";
        
        Technology technology = Technology.builder()
                .type(TechnologyType.BACKEND)
                .name("SpringBoot")
                .build();
        
        Version version = Version.fromString("1.2.3");
        
        GenericComponent component = GenericComponent.builder()
                .uuid(uuid)
                .name(name)
                .description(description)
                .type(type)
                .certificate(certificate)
                .technology(technology)
                .version(version)
                .creationDatetime(LocalDateTime.now())
                .build();

        // Act
        ItemDto result = mapper.mapGenericComponentToItemDto(component);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.uuid());
        assertEquals(name, result.name());
        assertEquals(description, result.description());
        assertEquals("SOFTWARE", result.type().label());
        assertEquals(3, result.attributes().size());
        
        // Check attributes
        boolean hasCertificate = result.attributes().stream()
                .anyMatch(attr -> "Certificate".equals(attr.getLabel()) && "Cert123".equals(attr.getValue()));
        boolean hasTechnology = result.attributes().stream()
                .anyMatch(attr -> "Technology".equals(attr.getLabel()) && "SpringBoot".equals(attr.getValue()));
        boolean hasVersion = result.attributes().stream()
                .anyMatch(attr -> "Version".equals(attr.getLabel()) && "1.2.3".equals(attr.getValue()));
        
        assertTrue(hasCertificate);
        assertTrue(hasTechnology);
        assertTrue(hasVersion);
        assertNotNull(result.createdDate());
    }

    @Test
    void shouldHandleRoundTripConversion() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Round Trip Component";
        String description = "Test Description";
        ItemTypeDto type = new ItemTypeDto("SOFTWARE");
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder()
                .label("Certificate")
                .value("Cert456")
                .build());
        attributes.add(AttributeDto.builder()
                .label("Technology")
                .value("Reactjs")
                .build());
        attributes.add(AttributeDto.builder()
                .label("Version")
                .value("2.3.4")
                .build());

        LocalDateTime createdDate = LocalDateTime.now();
        UUID createdBy = UUID.randomUUID();
        
        ItemDto originalItemDto = new ItemDto(
                uuid,
                name,
                description,
                type,
                attributes,
                null,
                null,
                createdDate,
                createdBy,
                createdBy,
                createdDate
        );

        // Act - Convert to GenericComponent and back to ItemDto
        GenericComponent genericComponent = mapper.mapItemDtoToGenericComponent(originalItemDto);
        ItemDto resultItemDto = mapper.mapGenericComponentToItemDto(genericComponent);

        // Assert
        assertNotNull(resultItemDto);
        assertEquals(originalItemDto.uuid(), resultItemDto.uuid());
        assertEquals(originalItemDto.name(), resultItemDto.name());
        assertEquals(originalItemDto.description(), resultItemDto.description());
        assertEquals(originalItemDto.type().label(), resultItemDto.type().label());
        
        // Check that attributes are preserved
        assertEquals(3, resultItemDto.attributes().size());
        assertTrue(resultItemDto.getMapAttributes().containsKey("Certificate"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("Technology"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("Version"));
    }

    @Test
    void shouldHandleDifferentComponentTypes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Hardware Component";
        String description = "Test Hardware";
        ItemTypeDto type = new ItemTypeDto("HARDWARE");
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder()
                .label("Certificate")
                .value("HardwareCert")
                .build());
        attributes.add(AttributeDto.builder()
                .label("Technology")
                .value("Linux")
                .build());
        attributes.add(AttributeDto.builder()
                .label("Version")
                .value("5.0.1")
                .build());

        LocalDateTime createdDate = LocalDateTime.now();
        UUID createdBy = UUID.randomUUID();
        
        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                type,
                attributes,
                null,
                null,
                createdDate,
                createdBy,
                createdBy,
                createdDate
        );

        // Act
        GenericComponent result = mapper.mapItemDtoToGenericComponent(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(ComponentType.HARDWARE, result.getType());
        assertEquals("Linux", result.getTechnology().getName());
        assertEquals(Version.fromString("5.0.1"), result.getVersion());
    }

    @Test
    void shouldHandlePartialAttributes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Partial Component";
        String description = "Test Description";
        ItemTypeDto type = new ItemTypeDto("VIRTUAL_MACHINE");
        
        Set<AttributeDto> attributes = new HashSet<>();
        // Only add certificate, missing technology and version
        attributes.add(AttributeDto.builder()
                .label("Certificate")
                .value("PartialCert")
                .build());

        LocalDateTime createdDate = LocalDateTime.now();
        UUID createdBy = UUID.randomUUID();
        
        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                type,
                attributes,
                null,
                null,
                createdDate,
                createdBy,
                createdBy,
                createdDate
        );

        // Act
        GenericComponent result = mapper.mapItemDtoToGenericComponent(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(ComponentType.VIRTUAL_MACHINE, result.getType());
        assertEquals("PartialCert", result.getCertificate());
        assertNull(result.getTechnology());
        assertNull(result.getVersion());
    }

    @Test
    void shouldHandleComponentWithNullCertificate() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "No Cert Component";
        String description = "Test Description";
        ComponentType type = ComponentType.SOFTWARE;
        
        Technology technology = Technology.builder()
                .type(TechnologyType.DATABASE)
                .name("PostgreSQL")
                .build();
        
        Version version = Version.fromString("1.0.0");
        
        GenericComponent component = GenericComponent.builder()
                .uuid(uuid)
                .name(name)
                .description(description)
                .type(type)
                .certificate(null) // Null certificate
                .technology(technology)
                .version(version)
                .creationDatetime(LocalDateTime.now())
                .build();

        // Act
        ItemDto result = mapper.mapGenericComponentToItemDto(component);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.uuid());
        assertEquals(name, result.name());
        assertEquals(description, result.description());
        
        // Check that certificate attribute is present but with null value
        boolean hasCertificate = result.attributes().stream()
                .anyMatch(attr -> "Certificate".equals(attr.getLabel()));
        assertFalse(hasCertificate);
    }

    @Test
    void shouldHandleEmptyAttributesInItemDto() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Empty Attribs Component";
        String description = "Test Description";
        ItemTypeDto type = new ItemTypeDto("IOT");
        
        Set<AttributeDto> attributes = new HashSet<>();
        // Add attributes with null values
        attributes.add(AttributeDto.builder()
                .label("Certificate")
                .value(null)
                .build());
        attributes.add(AttributeDto.builder()
                .label("Technology")
                .value(null)
                .build());
        attributes.add(AttributeDto.builder()
                .label("Version")
                .value(null)
                .build());

        LocalDateTime createdDate = LocalDateTime.now();
        UUID createdBy = UUID.randomUUID();
        
        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                type,
                attributes,
                null,
                null,
                createdDate,
                createdBy,
                createdBy,
                createdDate
        );

        // Act
        GenericComponent result = mapper.mapItemDtoToGenericComponent(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(ComponentType.IOT, result.getType());
        assertNull(result.getCertificate());
        assertNull(result.getTechnology());
        assertNull(result.getVersion());
    }

    @Test
    void shouldHandleDifferentTechnologyTypes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Database Component";
        String description = "Test Database";
        ItemTypeDto type = new ItemTypeDto("SOFTWARE");
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder()
                .label("Certificate")
                .value("DBCert")
                .build());
        attributes.add(AttributeDto.builder()
                .label("Technology")
                .value("Postgresl")
                .build());
        attributes.add(AttributeDto.builder()
                .label("Version")
                .value("10.1.0")
                .build());

        LocalDateTime createdDate = LocalDateTime.now();
        UUID createdBy = UUID.randomUUID();
        
        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                type,
                attributes,
                null,
                null,
                createdDate,
                createdBy,
                createdBy,
                createdDate
        );

        // Act
        GenericComponent result = mapper.mapItemDtoToGenericComponent(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals("Postgresl", result.getTechnology().getName());
        assertEquals(Version.fromString("10.1.0"), result.getVersion());
    }

    @Test
    void shouldHandleComponentWithAllNullOptionalFields() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Minimal Component";
        String description = "Test Description";
        ComponentType type = ComponentType.HOST;
        
        GenericComponent component = GenericComponent.builder()
                .uuid(uuid)
                .name(name)
                .description(description)
                .type(type)
                .certificate(null)
                .technology(null)
                .version(null)
                .creationDatetime(LocalDateTime.now())
                .build();

        // Act
        ItemDto result = mapper.mapGenericComponentToItemDto(component);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.uuid());
        assertEquals(name, result.name());
        assertEquals(description, result.description());
        assertEquals("HOST", result.type().label());
        
        // Should still create 0 attributes even if values are null
        assertEquals(0, result.attributes().size());
    }
}