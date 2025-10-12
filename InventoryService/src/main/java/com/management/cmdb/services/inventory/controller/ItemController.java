package com.management.cmdb.services.inventory.controller;

import com.management.cmdb.services.inventory.dto.ItemDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.inventory.model.UserDetail;
import com.management.cmdb.services.inventory.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/item")
public class ItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable UUID itemId) {
        LOGGER.info("Get item by id: {}", itemId);

        return ResponseEntity.ok(itemService.findItemById(itemId, UserDetail.UNKNOWN));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<ItemDto>> getItems(
            @RequestParam(name = "itemName", required = false) String itemName,
            @RequestParam(name = "itemType", required = false) String itemType,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize
            ) {
        LOGGER.info("Get items [itemName={}, attributeType={}]", itemName, itemType);
        return ResponseEntity.ok(itemService.searchItemByNameOrType(itemName, itemType, page, pageSize, UserDetail.UNKNOWN));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto itemDto) {
        LOGGER.info("Create item: {}", itemDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.itemService.createItem(itemDto, UserDetail.UNKNOWN));
    }

    @PutMapping
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto) {
        LOGGER.info("Update item: {}", itemDto);
        return ResponseEntity.ok(
                this.itemService.updateItem(itemDto, UserDetail.UNKNOWN)
        );
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID itemId) {
        LOGGER.info("Delete item: {}", itemId);
        this.itemService.deleteItem(itemId, UserDetail.UNKNOWN);
        return ResponseEntity.ok().build();
    }
}
