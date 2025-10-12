package com.management.cmdb.gateway.controller;

import com.management.cmdb.gateway.services.inventory.InventoryServiceClient;
import com.management.cmdb.gateway.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.gateway.services.inventory.dto.wrapper.PaginatedResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item-type")
public class ItemTypeController {

    private final InventoryServiceClient inventoryServiceClient;

    public ItemTypeController(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @GetMapping
    public PaginatedResponseDto<ItemTypeDto> getAllItemTypes() {
        return this.inventoryServiceClient.searchItemTypes("HOST", 0, 10);
    }
}
