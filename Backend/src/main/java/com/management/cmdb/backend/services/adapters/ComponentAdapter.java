package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.core.models.business.component.*;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.TechnologyType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.ports.outputs.ComponentOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.metadata.ItemMetadata;

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
                AttributeDto::label,
                AttributeDto::value
        ));
        switch (itemDto.type().label()){
            case "Host":
//                ItemDto technology = inventoryServiceClient.getOneItem(UUID.fromString(attributes.get("technology")))
//                        .map(technologyDto -> {
//                            TechnologyType.valueOf(technologyDto.attributes().)
//                            return new Technology(technologyDto.name(), technologyDto.description(), );
//                        })
//                        .orElseThrow(() -> new NotFoundException(String.format("Not able to find component technology [uuid=%s]", uuid)));
//
//                return new Host(
//                        itemDto.name(),
//                        itemDto.description(),
//                        ComponentType.valueOf(itemDto.type().label()),
//                        Version.fromString(attributes.get("version")),
//                        attributes.get("certificate"),
//                        new Technology(attributes.get("technology"), )
//                );
            default:
                throw new CoreException("Unsupported component type: " + itemDto.type().label());
        }
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
