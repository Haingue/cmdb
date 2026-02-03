package com.management.cmdb.backend.endpoint.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.cmdb.backend.services.adapters.ComponentAdapter;
import com.management.cmdb.backend.services.adapters.ComponentPersistentAdapter;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.GenericComponent;
import com.management.cmdb.core.models.business.component.Hardware;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.constant.ComponentType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/service/component")
public class ComponentController {

    private final InventoryServiceClient inventoryServiceClient;
    private final ComponentAdapter componentAdapter;
    private final ComponentPersistentAdapter componentPersistentAdapter;

    public ComponentController(InventoryServiceClient inventoryServiceClient, ComponentAdapter componentAdapter, ComponentPersistentAdapter componentPersistentAdapter) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.componentAdapter = componentAdapter;
        this.componentPersistentAdapter = componentPersistentAdapter;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Component> getComponent(@PathVariable UUID uuid) {
        Optional<Component> result = componentAdapter.findOne(uuid);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/project/{uuid}")
    public ResponseEntity<Host> getHost(@PathVariable UUID uuid) {
        Optional<Host> result = inventoryServiceClient.getOneHostItem(uuid);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<Host> createHost(@RequestBody Host host) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(host);
    }
}
