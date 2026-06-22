package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.constant.ActiveDirectoryDomainName;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.NetworkArea;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ComponentPersistentAdapterTest {

    @InjectMocks
    ComponentPersistentAdapter componentPersistentAdapter;

    @Mock
    InventoryServiceClient inventoryServiceClient;


    @Test
    void shouldSavedHost () throws UnknownHostException {
        // Arrange
        Component originalComponent = Host.builder()
                .type(ComponentType.HOST)
                .uuid(UUID.randomUUID())
                .name("test-host")
                .description("test-description")
                .technology(Technology.builder().build())
                .version(Version.fromString("0.0.1"))
                .dns("test-dns.com")
                .domain(ActiveDirectoryDomainName.COMMON)
                .networkArea(NetworkArea.DMZ)
                .ipAddress(InetAddress.getLocalHost())
                .creationDatetime(LocalDateTime.of(2020, 1, 1, 0, 0))
                .build();
        given(inventoryServiceClient.searchItemTypes(ComponentType.HOST.name(), 0, 1))
                .willReturn(new PaginatedResponseDto<>(List.of(
                        new ItemTypeDto(ComponentType.HOST.name())
                ), 0, 1, 1, 1, true));
        given(inventoryServiceClient.createItem(any(ItemDto.class))).willReturn(Optional.empty());

        // Act
        Component result = this.componentPersistentAdapter.accept(originalComponent);

        // Assert
        assertNotNull(result);
        assertEquals(originalComponent.getUuid(), result.getUuid());
        assertEquals(originalComponent.getName(), result.getName());
        assertEquals(originalComponent.getDescription(), result.getDescription());
    }
}