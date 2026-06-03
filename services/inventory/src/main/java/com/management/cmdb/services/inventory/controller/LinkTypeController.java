package com.management.cmdb.services.inventory.controller;

import com.management.cmdb.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.inventory.model.UserDetail;
import com.management.cmdb.services.inventory.service.LinkTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/link-type")
public class LinkTypeController {

    private final static Logger LOGGER = LoggerFactory.getLogger(LinkTypeController.class);

    private final LinkTypeService linkTypeService;

    public LinkTypeController(LinkTypeService linkTypeService) {
        this.linkTypeService = linkTypeService;
    }

    @GetMapping("/{linkTypeId}")
    public ResponseEntity<LinkTypeDto> getOne(@PathVariable UUID linkTypeId) {
        LOGGER.debug("Getting link type with id {}", linkTypeId);
        Optional<LinkTypeDto> result = linkTypeService.findById(linkTypeId);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<LinkTypeDto>> getSearchAllLinkTypes(
            @RequestParam(name = "label", required = false) String label,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        LOGGER.debug("Search for LinkTypes with label '{}' [pageNumber={}, pageSize {}]", label, pageNumber, pageSize);
        return ResponseEntity.ok(linkTypeService.search(label, pageNumber, pageSize));
    }

    @PostMapping
    public ResponseEntity<LinkTypeDto> createLinkType(@RequestBody LinkTypeDto linkTypeDto) {
        LOGGER.info("Creating new link type {}", linkTypeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(linkTypeService.create(linkTypeDto, UserDetail.UNKNOWN));
    }
}
