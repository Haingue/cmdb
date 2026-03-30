package com.management.cmdb.backend.endpoint.environment;

import com.management.cmdb.backend.endpoint.environment.dto.EnvironmentCreationRequest;
import com.management.cmdb.backend.endpoint.environment.dto.EnvironmentDto;
import com.management.cmdb.backend.endpoint.environment.mapper.EnvironmentMapper;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.ports.inputs.EnvironmentInputPort;
import com.management.cmdb.core.ports.inputs.ProjectInputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/service/environment")
public class EnvironmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentController.class);

    private final InventoryServiceClient inventoryServiceClient;
    private final EnvironmentInputPort  environmentService;
    private final ProjectInputPort projectService;

    public EnvironmentController(InventoryServiceClient inventoryServiceClient, EnvironmentInputPort environmentService, ProjectInputPort projectService) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.environmentService = environmentService;
        this.projectService = projectService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<EnvironmentDto> getEnvironment(@PathVariable UUID uuid) {
        return inventoryServiceClient.getOneEnvironmentItem(uuid)
                .map(EnvironmentMapper.INSTANCE::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping
    public ResponseEntity<EnvironmentDto> createEnvironment(@RequestBody EnvironmentCreationRequest request) {
        LOGGER.info("New environment creation request: {}", request);
        Environment environment = EnvironmentMapper.INSTANCE.toCoreModel(request.getEnvironment());
        environment.checkIntegrity();
        Project project = projectService.findOne(request.getProjectUuid(), request.getRequestor());
        environment = environmentService.create(request.getProjectUuid(), project.getShortName(), environment.getDescription(), environment.getLocation(), environment.getType(), environment.getJiraTracker(), User.UNKNONW);
        Optional<Environment> result = Optional.of(environment);
        return result.map(EnvironmentMapper.INSTANCE::toDto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping
    public ResponseEntity<EnvironmentDto> updateEnvironment(@RequestBody EnvironmentDto environmentDto) {
        LOGGER.info("Update environment creation request: {}", environmentDto);
        Environment environment = EnvironmentMapper.INSTANCE.toCoreModel(environmentDto);
        environment.checkIntegrity();
        Optional<Environment> result = inventoryServiceClient.updateItem(environment);
        return result.map(EnvironmentMapper.INSTANCE::toDto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
