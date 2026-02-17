package com.management.cmdb.backend.endpoint.project;

import com.management.cmdb.backend.endpoint.businessservice.mapper.BusinessServiceMapper;
import com.management.cmdb.backend.endpoint.project.dto.ProjectCreationRequest;
import com.management.cmdb.backend.endpoint.project.dto.ProjectDto;
import com.management.cmdb.backend.endpoint.project.mapper.ProjectMapper;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.core.models.business.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/service/project")
public class ProjectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    private final InventoryServiceClient inventoryServiceClient;

    public ProjectController(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable UUID uuid) {
        return inventoryServiceClient.getOneProjectItem(uuid)
                .map(ProjectMapper.INSTANCE::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectCreationRequest request) {
        LOGGER.info("New project creation request: {}", request);
        Project project = ProjectMapper.INSTANCE.toCoreModel(request.getProject());
        project.setBusinessService(BusinessServiceMapper.INSTANCE.toCoreModel(request.getBusinessService()));
        // project.checkIntegrity();
        // TODO enable groups creation
        Optional<Project> result = inventoryServiceClient.createItem(project);
        return result.map(ProjectMapper.INSTANCE::toDto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping
    public ResponseEntity<ProjectDto> updateProject(@RequestBody ProjectCreationRequest request) {
        LOGGER.info("Update project creation request: {}", request);
        Project project = ProjectMapper.INSTANCE.toCoreModel(request.getProject());
        project.setBusinessService(BusinessServiceMapper.INSTANCE.toCoreModel(request.getBusinessService()));
        project.checkIntegrity();
        Optional<Project> result = inventoryServiceClient.updateItem(project);
        return result.map(ProjectMapper.INSTANCE::toDto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
