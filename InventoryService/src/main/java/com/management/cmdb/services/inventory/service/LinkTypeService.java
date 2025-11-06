package com.management.cmdb.services.inventory.service;

import com.management.cmdb.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.inventory.model.UserDetail;

import java.util.Optional;
import java.util.UUID;

public interface LinkTypeService {

    LinkTypeDto create(LinkTypeDto linkTypeDto, UserDetail creator);

    Optional<LinkTypeDto> findById(UUID uuid);
    Optional<LinkTypeDto> findByLabel(String label);
    PaginatedResponseDto<LinkTypeDto> search(String label, int page, int size);
}
