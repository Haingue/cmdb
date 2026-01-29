package com.management.cmdb.backend.controller;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/item")
public class ItemController {

    private final InventoryServiceClient inventoryServiceClient;

    public ItemController(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<ItemDto>> searchItem(
            @RequestParam(required = false) String label,
            @RequestParam(required = false) String itemTypeLabel,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        PaginatedResponseDto<ItemDto> searchResults = this.inventoryServiceClient.searchItems(label, itemTypeLabel, pageNumber, pageSize);
        return ResponseEntity.ok(searchResults);
    }
}
