package com.management.cmdb.inventory.service.controller;

import com.management.cmdb.inventory.service.dto.ItemTypeDto;
import com.management.cmdb.inventory.service.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.inventory.service.mapper.ItemTypeMapper;
import com.management.cmdb.inventory.service.model.UserDetail;
import com.management.cmdb.inventory.service.service.ItemTypeService;
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
        return ResponseEntity.ok(itemTypeService.findById(itemTypeId));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<ItemTypeDto>> getAllItemTypes(
            @RequestParam(name = "label", required = false) String label,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(itemTypeService.search(label, page, pageSize));
    }

    @PostMapping
    public ResponseEntity<ItemTypeDto> createItemType(@RequestBody ItemTypeDto itemTypeDto) {
        LOGGER.info("Creating new item type {}", itemTypeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemTypeService.create(itemTypeDto, UserDetail.UNKNOWN));
    }
}
