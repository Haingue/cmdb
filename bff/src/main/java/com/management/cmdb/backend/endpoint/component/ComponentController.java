package com.management.cmdb.backend.endpoint.component;

import com.management.cmdb.backend.services.adapters.ComponentAdapter;
import com.management.cmdb.backend.services.inventory.mapper.component.ComponentMapperFactory;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.Host;
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

    public ComponentController(InventoryServiceClient inventoryServiceClient, ComponentAdapter componentAdapter) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.componentAdapter = componentAdapter;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Component> getComponent(@PathVariable UUID uuid) {
        Optional<Component> result = componentAdapter.findOne(uuid);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/project/{uuid}")
    public ResponseEntity<Host> getHost(@PathVariable UUID uuid) {
        Host result = inventoryServiceClient.getOneHostItem(uuid);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Host> createHost(@RequestBody Host host) {
        // TODO
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(host);
    }
}
