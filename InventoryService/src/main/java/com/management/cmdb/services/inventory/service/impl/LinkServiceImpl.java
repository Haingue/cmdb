package com.management.cmdb.services.inventory.service.impl;

import com.management.cmdb.services.inventory.dto.LinkDto;
import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import com.management.cmdb.services.inventory.entity.LinkTypeEntity;
import com.management.cmdb.services.inventory.job.StartupJob;
import com.management.cmdb.services.inventory.mapper.LinkMapper;
import com.management.cmdb.services.inventory.model.UserDetail;
import com.management.cmdb.services.inventory.repository.ItemRepository;
import com.management.cmdb.services.inventory.repository.LinkRepository;
import com.management.cmdb.services.inventory.repository.LinkTypeRepository;
import com.management.cmdb.services.inventory.service.LinkService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkRepository linkRepository;
    private final LinkTypeRepository linkTypeRepository;
    private final ItemRepository itemRepository;

    public LinkServiceImpl(LinkRepository linkRepository, LinkTypeRepository linkTypeRepository, ItemRepository itemRepository) {
        this.linkRepository = linkRepository;
        this.linkTypeRepository = linkTypeRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Optional<LinkDto> connectEntities(LinkDto linkDto, UserDetail requestor) {
        LinkEntity linkEntity = LinkMapper.INSTANCE.toEntity(linkDto);
        return connectEntities(linkEntity.getSourceItem(), linkEntity.getTargetItem(), linkEntity.getLinkType().getLabel(), linkEntity.getDescription())
                .map(LinkMapper.INSTANCE::toDto);
    }

    public Optional<LinkEntity> connectEntities(ItemEntity itemSource, ItemEntity itemTarget, String linkTypeLabel, String itemDescription) {
        LinkTypeEntity linkType = linkTypeRepository.findFirstByLabelIgnoreCase(linkTypeLabel).orElse(StartupJob.UNDEFINED);

        LinkEntity linkEntity = linkRepository.findFirstBySourceItemUuidAndTargetItemUuidAndLinkTypeLabel(itemSource.getUuid(), itemTarget.getUuid(), linkType.getLabel())
            .orElseGet(() -> {
                LinkEntity newLinkEntity = new LinkEntity();
                newLinkEntity.setUuid(UUID.randomUUID());
                newLinkEntity.setLinkType(linkType);
                return newLinkEntity;
            });
        linkEntity.setLinkType(linkType);
        linkEntity.setSourceItem(itemSource);
        linkEntity.setTargetItem(itemTarget);
        linkEntity.setDescription(itemDescription);

        linkEntity = linkRepository.save(linkEntity);

        return Optional.of(linkEntity);
    }

    @Override
    public Optional<LinkDto> disconnectEntities(LinkDto linkDto, UserDetail requestor) {
        return Optional.empty();
    }

}
