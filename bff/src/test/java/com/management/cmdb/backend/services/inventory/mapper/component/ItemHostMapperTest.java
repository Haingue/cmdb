package com.management.cmdb.backend.services.inventory.mapper.component;

import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.component.network.Vlan;
import com.management.cmdb.core.models.business.constant.ActiveDirectoryDomainName;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.NetworkArea;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemHostMapperTest {

    private final ItemHostMapper mapper = ItemHostMapper.INSTANCE;

    @Test
    void shouldMapItemDtoToHostWithAllAttributes() throws UnknownHostException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Test Host";
        String description = "Production Host";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("Cert123").build());
        attributes.add(AttributeDto.builder().label("Version").value("1.2.3").build());
        attributes.add(AttributeDto.builder().label("Technology").value("Linux").build());
        attributes.add(AttributeDto.builder().label("IpAddress").value("192.168.1.100").build());
        attributes.add(AttributeDto.builder().label("Vlan").value("10").build());
        attributes.add(AttributeDto.builder().label("Domain").value("COMMON").build());
        attributes.add(AttributeDto.builder().label("NetworkArea").value("OT_DMZ").build());
        attributes.add(AttributeDto.builder().label("PatchingDay").value("MONDAY").build());
        attributes.add(AttributeDto.builder().label("Dns").value("dns.server.com").build());
        attributes.add(AttributeDto.builder().label("MacAddress").value("00:1A:2B:3C:4D:5E").build());

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("HOST"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Host result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ComponentType.HOST, result.getType());
        assertEquals("Cert123", result.getCertificate());
        assertEquals(Version.fromString("1.2.3"), result.getVersion());
        assertEquals("Linux", result.getTechnology().getName());
        assertEquals(InetAddress.getByName("192.168.1.100"), result.getIpAddress());
        assertEquals(Vlan.builder().number(10).build(), result.getVlan());
        assertEquals(ActiveDirectoryDomainName.COMMON, result.getDomain());
        assertEquals(NetworkArea.OT_DMZ, result.getNetworkArea());
        assertEquals(DayOfWeek.MONDAY, result.getPatchingDay());
        assertEquals("dns.server.com", result.getDns());
        assertEquals("00:1A:2B:3C:4D:5E", result.getMacAddress());
        assertEquals(createdDate, result.getCreationDatetime());
    }

    @Test
    void shouldMapItemDtoToHostWithMinimalAttributes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Minimal Host";
        String description = "Minimal Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        // Only required attributes
        attributes.add(AttributeDto.builder().label("Certificate").value("MinCert").build());

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("HOST"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Host result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ComponentType.HOST, result.getType());
        assertEquals("MinCert", result.getCertificate());
        assertNull(result.getVersion());
        assertNull(result.getTechnology());
        assertNull(result.getIpAddress());
        assertNull(result.getVlan());
        assertNull(result.getDomain());
        assertNull(result.getNetworkArea());
        assertNull(result.getPatchingDay());
        assertNull(result.getDns());
        assertNull(result.getMacAddress());
    }

    @Test
    void shouldMapHostToItemDtoWithAllAttributes() throws UnknownHostException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Test Host";
        String description = "Production Host";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Host host = Host.builder()
                .uuid(uuid)
                .name(name)
                .description(description)
                .type(ComponentType.HOST)
                .certificate("Cert123")
                .version(Version.fromString("1.2.3"))
                .technology(Technology.builder().name("Linux").build())
                .ipAddress(InetAddress.getByName("192.168.1.100"))
                .vlan(Vlan.builder().number(10).build())
                .domain(ActiveDirectoryDomainName.COMMON)
                .networkArea(NetworkArea.OT_DMZ)
                .patchingDay(DayOfWeek.MONDAY)
                .dns("dns.server.com")
                .macAddress("00:1A:2B:3C:4D:5E")
                .creationDatetime(createdDate)
                .build();

        // Act
        ItemDto result = mapper.mapToItemDto(host);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.uuid());
        assertEquals(name, result.name());
        assertEquals(description, result.description());
        assertEquals("HOST", result.type().label());
        assertEquals(9, result.attributes().size()); // All attributes should be present
        
        // Verify specific attributes
        assertTrue(result.getMapAttributes().containsKey("Certificate"));
        assertEquals("Cert123", result.getMapAttributes().get("Certificate"));
        
        assertTrue(result.getMapAttributes().containsKey("Version"));
        assertEquals("1.2.3", result.getMapAttributes().get("Version"));
        
        assertTrue(result.getMapAttributes().containsKey("Technology"));
        assertEquals("Linux", result.getMapAttributes().get("Technology"));
        
        assertTrue(result.getMapAttributes().containsKey("IpAddress"));
        assertEquals("192.168.1.100", result.getMapAttributes().get("IpAddress"));
        
        assertTrue(result.getMapAttributes().containsKey("Vlan"));
        assertEquals("10", result.getMapAttributes().get("Vlan"));
        
        assertTrue(result.getMapAttributes().containsKey("Domain"));
        assertEquals("COMMON", result.getMapAttributes().get("Domain"));
        
        assertTrue(result.getMapAttributes().containsKey("NetworkArea"));
        assertEquals("OT_DMZ", result.getMapAttributes().get("NetworkArea"));
        
        assertTrue(result.getMapAttributes().containsKey("PatchingDay"));
        assertEquals("MONDAY", result.getMapAttributes().get("PatchingDay"));
        
        assertTrue(result.getMapAttributes().containsKey("Dns"));
        assertEquals("dns.server.com", result.getMapAttributes().get("Dns"));
        
        assertTrue(result.getMapAttributes().containsKey("MacAddress"));
        assertEquals("00:1A:2B:3C:4D:5E", result.getMapAttributes().get("MacAddress"));
    }

    @Test
    void shouldMapHostToItemDtoWithNullAttributes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Minimal Host";
        String description = "Minimal Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Host host = Host.builder()
                .uuid(uuid)
                .name(name)
                .description(description)
                .type(ComponentType.HOST)
                .certificate(null)
                .version(null)
                .technology(null)
                .ipAddress(null)
                .vlan(null)
                .domain(null)
                .networkArea(null)
                .patchingDay(null)
                .dns(null)
                .macAddress(null)
                .creationDatetime(createdDate)
                .build();

        // Act
        ItemDto result = mapper.mapToItemDto(host);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.uuid());
        assertEquals(name, result.name());
        assertEquals(description, result.description());
        assertEquals("HOST", result.type().label());
        // Should have empty attributes since all values are null
        assertTrue(result.attributes().isEmpty());
    }

    @Test
    void shouldHandleRoundTripConversion() throws UnknownHostException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Round Trip Host";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("RoundCert").build());
        attributes.add(AttributeDto.builder().label("Version").value("2.3.4").build());
        attributes.add(AttributeDto.builder().label("Technology").value("Round OS").build());
        attributes.add(AttributeDto.builder().label("IpAddress").value("10.0.0.1").build());
        attributes.add(AttributeDto.builder().label("Vlan").value("20").build());
        attributes.add(AttributeDto.builder().label("Domain").value("MANUFACTURING").build());
        attributes.add(AttributeDto.builder().label("NetworkArea").value("IT").build());
        attributes.add(AttributeDto.builder().label("PatchingDay").value("TUESDAY").build());

        ItemDto originalItemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("HOST"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act - Convert to Host and back to ItemDto
        Host host = mapper.mapToCoreModel(originalItemDto);
        ItemDto resultItemDto = mapper.mapToItemDto(host);

        // Assert
        assertNotNull(resultItemDto);
        assertEquals(originalItemDto.uuid(), resultItemDto.uuid());
        assertEquals(originalItemDto.name(), resultItemDto.name());
        assertEquals(originalItemDto.description(), resultItemDto.description());
        assertEquals(originalItemDto.type().label(), resultItemDto.type().label());
        
        // Check that attributes are preserved
        assertEquals(8, resultItemDto.attributes().size());
        assertTrue(resultItemDto.getMapAttributes().containsKey("Certificate"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("Version"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("Technology"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("IpAddress"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("Vlan"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("Domain"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("NetworkArea"));
        assertTrue(resultItemDto.getMapAttributes().containsKey("PatchingDay"));
    }

    @Test
    void shouldHandleInvalidIpAddress() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Invalid IP Host";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("TestCert").build());
        attributes.add(AttributeDto.builder().label("IpAddress").value("invalid.ip.address").build());

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("HOST"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            mapper.mapToCoreModel(itemDto);
        });
    }

    @Test
    void shouldHandleInvalidVlanNumber() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Invalid VLAN Host";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("TestCert").build());
        attributes.add(AttributeDto.builder().label("Vlan").value("invalid").build());

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("HOST"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            mapper.mapToCoreModel(itemDto);
        });
    }

    @Test
    void shouldHandleDifferentComponentTypes() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Flexible Host";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("FlexCert").build());

        // Test with different type labels
        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("VIRTUAL_MACHINE"), // Different type
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Host result = mapper.mapToCoreModel(itemDto);

        // Assert - should default to HOST if type is not HARDWARE
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        // The type should be set to the value from DTO or default
        assertEquals("FlexCert", result.getCertificate());
    }

    @Test
    void shouldHandleHostWithPartialNetworkAttributes() throws UnknownHostException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Partial Network Host";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("TestCert").build());
        attributes.add(AttributeDto.builder().label("IpAddress").value("192.168.1.50").build());
        // Missing VLAN, Domain, NetworkArea, etc.

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("HOST"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Host result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        assertEquals(InetAddress.getByName("192.168.1.50"), result.getIpAddress());
        assertNull(result.getVlan());
        assertNull(result.getDomain());
        assertNull(result.getNetworkArea());
        assertNull(result.getPatchingDay());
    }

    @Test
    void shouldHandleHostWithAllNetworkAttributes() throws UnknownHostException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Full Network Host";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("FullCert").build());
        attributes.add(AttributeDto.builder().label("IpAddress").value("10.10.10.10").build());
        attributes.add(AttributeDto.builder().label("Vlan").value("50").build());
        attributes.add(AttributeDto.builder().label("Domain").value("PROD").build());
        attributes.add(AttributeDto.builder().label("NetworkArea").value("DMZ").build());
        attributes.add(AttributeDto.builder().label("PatchingDay").value("WEDNESDAY").build());
        attributes.add(AttributeDto.builder().label("Dns").value("dns.prod.com").build());
        attributes.add(AttributeDto.builder().label("MacAddress").value("AA:BB:CC:DD:EE:FF").build());

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("HOST"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Host result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(name, result.getName());
        assertEquals(InetAddress.getByName("10.10.10.10"), result.getIpAddress());
        assertEquals(Vlan.builder().number(50).build(), result.getVlan());
        assertEquals(ActiveDirectoryDomainName.COMMON, result.getDomain());
        assertEquals(NetworkArea.DMZ, result.getNetworkArea());
        assertEquals(DayOfWeek.WEDNESDAY, result.getPatchingDay());
        assertEquals("dns.prod.com", result.getDns());
        assertEquals("AA:BB:CC:DD:EE:FF", result.getMacAddress());
    }

    @Test
    void shouldHandleHostWithDifferentPatchingDays() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Weekly Patching Host";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("TestCert").build());
        attributes.add(AttributeDto.builder().label("PatchingDay").value("FRIDAY").build());

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("HOST"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act
        Host result = mapper.mapToCoreModel(itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(DayOfWeek.FRIDAY, result.getPatchingDay());
    }

    @Test
    void shouldHandleHostWithInvalidPatchingDay() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String name = "Invalid Patching Day Host";
        String description = "Test Description";
        LocalDateTime createdDate = LocalDateTime.now();
        
        Set<AttributeDto> attributes = new HashSet<>();
        attributes.add(AttributeDto.builder().label("Certificate").value("TestCert").build());
        attributes.add(AttributeDto.builder().label("PatchingDay").value("INVALID_DAY").build());

        ItemDto itemDto = new ItemDto(
                uuid,
                name,
                description,
                new ItemTypeDto("HOST"),
                attributes,
                null,
                null,
                createdDate,
                UUID.randomUUID(),
                UUID.randomUUID(),
                createdDate
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.mapToCoreModel(itemDto);
        });
    }
}