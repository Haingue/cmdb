package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.core.models.business.component.*;
import com.management.cmdb.core.models.business.component.network.Vlan;
import com.management.cmdb.core.models.business.constant.ActiveDirectoryDomainName;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.NetworkArea;
import com.management.cmdb.core.models.business.constant.TechnologyType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.ports.outputs.ComponentOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.metadata.ItemMetadata;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ComponentAdapter implements ComponentOutputPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentAdapter.class);

    private final InventoryServiceClient inventoryServiceClient;

    public ComponentAdapter(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Override
    public Optional<Component> findOne(UUID uuid) {
        ItemDto itemDto = inventoryServiceClient.getOneItem(uuid)
                .orElseThrow(() -> new NotFoundException(uuid));

        Map<String, String> attributes = itemDto.attributes().stream().collect(Collectors.toMap(
                AttributeDto::getLabel,
                AttributeDto::getValue
        ));
        switch (itemDto.type().label()){
            case "Host":
                Host.builder()
                        .uuid(uuid)
                        .name(itemDto.name())
                        .description(itemDto.description())
                        .type(ComponentType.valueOf(attributes.get("type")))
                        .version(Version.fromString(attributes.get("version")))
                        .technology(Technology.builder().name(attributes.get("technology")).build())
                        .certificate(attributes.get("certificate"))
                        .build();
            case "Software":
                Software.builder()
                        .uuid(uuid)
                        .name(itemDto.name())
                        .description(itemDto.description())
                        .type(ComponentType.valueOf(attributes.get("type")))
                        .version(Version.fromString(attributes.get("version")))
                        .technology(Technology.builder().name(attributes.get("technology")).build())
                        .certificate(attributes.get("certificate"))
                        .host(Host.builder().name(attributes.get("host")).build())
                        .build();
            case "Hardware":
                try {
                    Hardware.builder()
                            .uuid(uuid)
                            .name(itemDto.name())
                            .description(itemDto.description())
                            .type(ComponentType.valueOf(attributes.get("type")))
                            .version(Version.fromString(attributes.get("version")))
                            .technology(Technology.builder().name(attributes.get("technology")).build())
                            .certificate(attributes.get("certificate"))
                            .location(attributes.get("location"))
                            .dns(attributes.get("dns"))
                            .macAddress(attributes.get("macAddress"))
                            .ipAddress(InetAddress.getByAddress(attributes.get("ipAddress").getBytes()))
                            .vlan(Vlan.builder().number(Integer.parseInt(attributes.get("vlan"))).build())
                            .domain(ActiveDirectoryDomainName.valueOf(attributes.get("domain")))
                            .networkArea(NetworkArea.valueOf(attributes.get("networkArea")))
                            .patchingDay(DayOfWeek.valueOf(attributes.get("patchingDay")))
                            .build();
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            default:
                GenericComponent.builder()
                        .uuid(uuid)
                        .name(itemDto.name())
                        .description(itemDto.description())
                        .type(ComponentType.valueOf(attributes.get("type")))
                        .version(Version.fromString(attributes.get("version")))
                        .technology(Technology.builder().name(attributes.get("technology")).build())
                        .certificate(attributes.get("certificate"))
                        .build();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Component> findOneByName(String s) {

        return Optional.empty();
    }

    @Override
    public void delete(Component component) {
        inventoryServiceClient.deleteItem(component.getUuid());
    }
}
