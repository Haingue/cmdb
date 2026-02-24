package com.management.cmdb.services.inventory.controller;

import com.management.cmdb.services.inventory.dto.LinkDto;
import com.management.cmdb.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.services.inventory.mapper.LinkMapper;
import com.management.cmdb.services.inventory.model.UserDetail;
import com.management.cmdb.services.inventory.service.LinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/link")
public class LinkController {

    private final static Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    public ResponseEntity<LinkDto> createLinkType(@RequestBody LinkDto linkDto) {
        LOGGER.info("Creating new link type {}", linkDto);
        Optional<LinkDto> result = linkService.connectEntities(linkDto, UserDetail.UNKNOWN);
        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }
}
