package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.backend.exceptions.AdapterException;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.LinkDto;
import com.management.cmdb.backend.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.outputs.EnvironmentOutputPort;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EnvironmentAdapter implements EnvironmentOutputPort {

    public static final String ITEM_TYPE_LABEL = "Environment";
    public static final String LINK_TYPE_COMPONENT = "Composed of";

    private final InventoryServiceClient inventoryServiceClient;

    public EnvironmentAdapter(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Override
    public Optional<Environment> findOne(UUID uuid) {
        return inventoryServiceClient.getOneEnvironmentItem(uuid);
    }

    @Override
    public Optional<Environment> findByComponent(UUID uuid) {
        throw new NotImplemented();
    }

    @Override
    public Environment save(Environment environment) {
        try {
            Optional<Environment> newItem = inventoryServiceClient.createItem(environment);
            // Save link to components
            if (!environment.getComponents().isEmpty()) {
                for (Component component: environment.getComponents()) {
                    LinkDto linkDto = new LinkDto(new LinkTypeDto(LINK_TYPE_COMPONENT), environment.getUuid(), component.getUuid(), "");
                    inventoryServiceClient.linkItems(linkDto);
                }
            }
            return newItem
                    .orElseThrow(AdapterException::new);
        } catch (FeignException exception) {
            return inventoryServiceClient.updateItem(environment)
                    .orElseThrow(AdapterException::new);
        }
    }

    @Override
    public void attachProject(Environment environment, UUID projectUuid) {
        LinkDto linkDto = new LinkDto(new LinkTypeDto(LINK_TYPE_COMPONENT), projectUuid, environment.getUuid(), "");
        inventoryServiceClient.linkItems(linkDto);
    }

    @Override
    public void detachProject(Environment environment, UUID projectUuid) {
        throw new NotImplemented();
    }

    @Override
    public void delete(Environment environment) {
        throw new NotImplemented();
    }
}
