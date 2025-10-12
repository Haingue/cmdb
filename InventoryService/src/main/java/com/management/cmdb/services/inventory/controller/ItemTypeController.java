package com.management.cmdb.services.inventory.controller;

import com.management.cmdb.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.inventory.model.UserDetail;
import com.management.cmdb.services.inventory.service.ItemTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/item-type")
public class ItemTypeController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ItemTypeController.class);

    private final ItemTypeService itemTypeService;

    public ItemTypeController(ItemTypeService itemTypeService) {
        this.itemTypeService = itemTypeService;
    }

    @GetMapping("/{itemTypeId}")
    public ResponseEntity<ItemTypeDto> getOne(@PathVariable UUID itemTypeId) {
        LOGGER.debug("Getting item type with id {}", itemTypeId);
        return ResponseEntity.ok(itemTypeService.findById(itemTypeId));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<ItemTypeDto>> getSearchAllItemTypes(
            @RequestParam(name = "label", required = false) String label,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        LOGGER.debug("Search for ItemTypes with label '{}' [pageNumber={}, pageSize {}]", label, pageNumber, pageSize);
        return ResponseEntity.ok(itemTypeService.search(label, pageNumber, pageSize));
    }

    @PostMapping
    public ResponseEntity<ItemTypeDto> createItemType(@RequestBody ItemTypeDto itemTypeDto) {
        LOGGER.info("Creating new item type {}", itemTypeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemTypeService.create(itemTypeDto, UserDetail.UNKNOWN));
    }
}
