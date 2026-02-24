package com.management.cmdb.services.inventory.service;

import com.management.cmdb.services.inventory.dto.LinkDto;
import com.management.cmdb.services.inventory.model.UserDetail;

import java.util.Optional;

public interface LinkService {

    Optional<LinkDto> connectEntities (LinkDto linkDto, UserDetail requestor);
    Optional<LinkDto> disconnectEntities (LinkDto linkDto, UserDetail requestor);

}
