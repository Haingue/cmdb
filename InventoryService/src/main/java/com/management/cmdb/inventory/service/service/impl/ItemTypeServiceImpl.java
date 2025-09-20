package com.management.cmdb.inventory.service.service.impl;

import com.management.cmdb.inventory.service.dto.ItemTypeDto;
import com.management.cmdb.inventory.service.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import com.management.cmdb.inventory.service.exception.ItemTypeNotExist;
import com.management.cmdb.inventory.service.mapper.ItemTypeMapper;
import com.management.cmdb.inventory.service.model.UserDetail;
import com.management.cmdb.inventory.service.repository.ItemTypeRepository;
import com.management.cmdb.inventory.service.service.ItemTypeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ItemTypeServiceImpl implements ItemTypeService {

    private final ItemTypeRepository itemTypeRepository;

    public ItemTypeServiceImpl(ItemTypeRepository itemTypeRepository) {
        this.itemTypeRepository = itemTypeRepository;
    }

    @Override
    public ItemTypeDto create(ItemTypeDto itemTypeDto, UserDetail author) {
        // TODO check user details

        if (ObjectUtils.isEmpty(itemTypeDto.label()) || ObjectUtils.isEmpty(itemTypeDto.description())) throw new ItemTypeNotExist();

        LocalDateTime creationDatetime = LocalDateTime.now();
        ItemTypeEntity newEntity = ItemTypeMapper.INSTANCE.toEntity(itemTypeDto);
        newEntity.setUuid(UUID.randomUUID());
        newEntity.setCreatedBy(author.uuid());
        newEntity.setCreatedDate(creationDatetime);

        newEntity.getAttributes().forEach(attribute -> {
            attribute.setUuid(UUID.randomUUID());
            attribute.setCreatedBy(author.uuid());
            attribute.setCreatedDate(creationDatetime);
        });

        newEntity = itemTypeRepository.save(newEntity);
        return ItemTypeMapper.INSTANCE.toDto(newEntity);
    }

    @Override
    public ItemTypeDto findById(UUID id) {
        return ItemTypeMapper.INSTANCE.toDto(
                itemTypeRepository.findById(id)
                        .orElseThrow(ItemTypeNotExist::new)
        );
    }

    @Override
    public PaginatedResponseDto<ItemTypeDto> search(String label, int page, int size) {
        return ItemTypeMapper.INSTANCE.toDto(
                itemTypeRepository.searchAllByLabelContainingIgnoreCase(label, PageRequest.of(page, size))
        );
    }
}
