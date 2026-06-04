package com.management.cmdb.backend.endpoint.businessservice.mapper;

import com.management.cmdb.backend.endpoint.businessservice.dto.LinkDto;
import com.management.cmdb.core.models.business.project.Link;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LinkMapper {
    LinkMapper INSTANCE = Mappers.getMapper(LinkMapper.class);

    LinkDto toDto(Link coreModel);
    Link toCoreModel(LinkDto dto);
}