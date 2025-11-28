package com.management.cmdb.backend.scripting;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class ScriptFunctions {


    private final InventoryServiceClient inventoryServiceClient;

    public ScriptFunctions(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public List<String> searchProjects(String projectName) {
        return inventoryServiceClient.searchItems(projectName, "PROJECT", 0, 10)
                .content().stream()
                .map(ItemTypeDto::label)
                .toList();
    }

    public Map<String, Object> getAvailableFunctions() {
        Function<String, List<String>> searchProjectsFunction = this::searchProjects;
        return Map.of(
                "searchProjects", searchProjectsFunction
        );
    }
}
