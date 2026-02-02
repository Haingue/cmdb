package com.management.cmdb.backend.endpoint.component;

import com.management.cmdb.backend.services.adapters.ComponentAdapter;
import com.management.cmdb.core.models.business.component.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/service/component")
public class ComponentController {

    private final ComponentAdapter componentAdapter;

    public ComponentController(ComponentAdapter componentAdapter) {
        this.componentAdapter = componentAdapter;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Component> getComponent(@PathVariable UUID uuid) {
        Optional<Component> result = componentAdapter.findOne(uuid);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
