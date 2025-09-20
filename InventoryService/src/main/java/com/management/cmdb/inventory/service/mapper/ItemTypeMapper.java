package com.management.cmdb.inventory.service.mapper;

import com.management.cmdb.inventory.service.dto.ItemDto;
import com.management.cmdb.inventory.service.dto.ItemTypeDto;
import com.management.cmdb.inventory.service.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ItemTypeMapper {
    ItemTypeMapper INSTANCE = Mappers.getMapper(ItemTypeMapper.class);

    ItemTypeDto toDto(ItemTypeEntity entity);
    ItemTypeEntity toEntity(ItemTypeDto dto);

    PaginatedResponseDto<ItemTypeDto> toDto(Page<ItemTypeEntity> page);
}
