package com.management.cmdb.backend.endpoint.environment;

import com.management.cmdb.backend.endpoint.businessservice.BusinessServiceController;
import com.management.cmdb.backend.endpoint.businessservice.mapper.BusinessServiceMapper;
import com.management.cmdb.backend.endpoint.environment.dto.EnvironmentDto;
import com.management.cmdb.backend.endpoint.environment.mapper.EnvironmentMapper;
import com.management.cmdb.backend.endpoint.project.dto.ProjectCreationRequest;
import com.management.cmdb.backend.endpoint.project.dto.ProjectDto;
import com.management.cmdb.backend.endpoint.project.mapper.ProjectMapper;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.service.EnvironmentService;
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

    public EnvironmentController(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<EnvironmentDto> getProject(@PathVariable UUID uuid) {
        Environment oneEnvironmentItem = inventoryServiceClient.getOneEnvironmentItem(uuid);
        return ResponseEntity.ok(EnvironmentMapper.INSTANCE.toDto(oneEnvironmentItem));
    }

    @PostMapping
    public ResponseEntity<EnvironmentDto> createProject(@RequestBody EnvironmentDto environmentDto) {
        LOGGER.info("New environment creation request: {}", environmentDto);
        Environment environment = EnvironmentMapper.INSTANCE.toCoreModel(environmentDto);
        environment.checkIntegrity();
        Optional<Environment> result = inventoryServiceClient.createItem(environment);
        return result.map(EnvironmentMapper.INSTANCE::toDto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
