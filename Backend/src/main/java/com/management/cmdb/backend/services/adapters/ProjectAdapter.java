package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.backend.exceptions.AdapterException;
import com.management.cmdb.backend.exceptions.UnsavedDependencyException;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.LinkDto;
import com.management.cmdb.backend.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.outputs.ProjectOutputPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectAdapter implements ProjectOutputPort {

    public static final String ITEM_TYPE_LABEL = "Project";
    public static final String LINK_TYPE_BUSINESS_SERVICE = "Implements";
    public static final String LINK_TYPE_ENVIRONMENT = "Deployed in";

    private final InventoryServiceClient inventoryServiceClient;

    public ProjectAdapter(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Override
    public Optional<Project> findOne(UUID uuid) {
        return inventoryServiceClient.getOneProjectItem(uuid);
    }

    @Override
    public Optional<Project> findOneByShortName(String s) {
        throw new NotImplemented();
    }

    @Override
    public Optional<Project> findByEnvironment(UUID uuid) {
        throw new NotImplemented();
    }

    @Override
    public Project save(Project project) {
        Optional<Project> newItem = inventoryServiceClient.createItem(project);
        if (newItem.isEmpty()) {
            return inventoryServiceClient.updateItem(project)
                    .orElseThrow(AdapterException::new);
        }

        // Save link to business service
        if (project.getBusinessService() != null) {
            ItemDto businessService = inventoryServiceClient.searchItems(project.getBusinessService().getName(), BusinessServiceAdapter.ITEM_TYPE_LABEL, 0, 1)
                    .content().stream()
                    .findFirst()
                    .orElseThrow(UnsavedDependencyException::new);
            LinkDto linkDto = new LinkDto(new LinkTypeDto(LINK_TYPE_BUSINESS_SERVICE), project.getUuid(), businessService.uuid(), "");
            inventoryServiceClient.linkItems(linkDto);
        }
        // Save link to environments
        if (!project.getEnvironments().isEmpty()) {
            for (Environment env: project.getEnvironments()) {
                LinkDto linkDto = new LinkDto(new LinkTypeDto(LINK_TYPE_ENVIRONMENT), project.getUuid(), env.getUuid(), "");
                inventoryServiceClient.linkItems(linkDto);
            }
        }

        return newItem
                .orElseThrow(AdapterException::new);
    }

    @Override
    public void delete(UUID uuid) {
        throw new NotImplemented();
    }
}
