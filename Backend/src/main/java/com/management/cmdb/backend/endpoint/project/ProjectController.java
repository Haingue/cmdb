package com.management.cmdb.backend.endpoint.project;

import com.management.cmdb.backend.endpoint.businessservice.mapper.BusinessServiceMapper;
import com.management.cmdb.backend.endpoint.project.dto.ProjectCreationRequest;
import com.management.cmdb.backend.endpoint.project.dto.ProjectDto;
import com.management.cmdb.backend.endpoint.project.mapper.ProjectMapper;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.service.ProjectService;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/service/project")
public class ProjectController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProjectController.class);

    private final InventoryServiceClient inventoryServiceClient;

    public ProjectController(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Project> getProject(@PathVariable UUID uuid) {
        return ResponseEntity.ok(inventoryServiceClient.getOneProjectItem(uuid));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createProject(@RequestBody ProjectCreationRequest request) {
        LOGGER.info("New project creation request: {}", request);
        ProjectDto projectDto = request.getProject();
        BusinessService businessService = BusinessServiceMapper.INSTANCE.toCoreModel(request.getBusinessService());
        ItemTypeDto projectItemType = inventoryServiceClient.searchItemTypes("Project", 0, 1).content().getFirst();
        String ownerUserGroups = null;
        if (projectDto.owners() != null) {
            ownerUserGroups = projectDto.owners().name();
        }
        String maintainerUserGroups = null;
        if (projectDto.maintainers() != null) {
            maintainerUserGroups = projectDto.maintainers().name();
        }
        ItemDto projectItem = new ItemDto(
                null,
                projectDto.fullName(),
                projectDto.description(),
                projectItemType,
                Set.of(
                    new AttributeDto(null, "FullName", null, projectDto.fullName(), null, null, null, null),
                    new AttributeDto(null, "ShortName", null, projectDto.shortName(), null, null, null, null),
                    new AttributeDto(null, "BusinessService", null, businessService.getName(), null, null, null, null),
                    new AttributeDto(null, "Owners", null, ownerUserGroups, null, null, null, null),
                    new AttributeDto(null, "Maintainers", null, maintainerUserGroups, null, null, null, null)
                ),
                Set.of(),
                Set.of(),
                null,
                null,
                null,
                null
        );
        Optional<ItemDto> result = inventoryServiceClient.createItem(projectItem);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
