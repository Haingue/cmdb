package com.management.cmdb.services.inventory.service.impl;

import com.management.cmdb.services.inventory.dto.AuthorDto;
import com.management.cmdb.services.inventory.dto.LinkDto;
import com.management.cmdb.services.inventory.dto.NotificationDto;
import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import com.management.cmdb.services.inventory.entity.LinkTypeEntity;
import com.management.cmdb.services.inventory.entity.PredefinedLinkType;
import com.management.cmdb.services.inventory.exception.LinkedItemDoesNotExist;
import com.management.cmdb.services.inventory.mapper.LinkMapper;
import com.management.cmdb.services.inventory.model.UserDetail;
import com.management.cmdb.services.inventory.repository.ItemRepository;
import com.management.cmdb.services.inventory.repository.LinkRepository;
import com.management.cmdb.services.inventory.repository.LinkTypeRepository;
import com.management.cmdb.services.inventory.service.LinkService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkRepository linkRepository;
    private final LinkTypeRepository linkTypeRepository;
    private final ItemRepository itemRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public LinkServiceImpl(LinkRepository linkRepository, LinkTypeRepository linkTypeRepository, ItemRepository itemRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.linkRepository = linkRepository;
        this.linkTypeRepository = linkTypeRepository;
        this.itemRepository = itemRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Optional<LinkDto> connectEntities(LinkDto linkDto, UserDetail requestor) {
        LinkEntity linkEntity = LinkMapper.INSTANCE.toEntity(linkDto);
        ItemEntity sourceItem = itemRepository.findById(linkDto.sourceItemId())
                .orElseThrow(() -> new LinkedItemDoesNotExist(linkDto.sourceItemId()));
        ItemEntity targetItem = itemRepository.findById(linkDto.targetItemId())
                .orElseThrow(() -> new LinkedItemDoesNotExist(linkDto.sourceItemId()));
        return connectEntities(sourceItem, targetItem, linkEntity.getLinkType().getLabel(), linkEntity.getDescription(), requestor)
                .map(LinkMapper.INSTANCE::toDto);
    }

    public Optional<LinkEntity> connectEntities(ItemEntity itemSource, ItemEntity itemTarget, String linkTypeLabel, String itemDescription, UserDetail requestor) {
        LinkTypeEntity linkType = linkTypeRepository.findFirstByLabelIgnoreCase(linkTypeLabel)
                .orElse(PredefinedLinkType.UNDEFINED_LINK_TYPE.getLinkTypeEntity());

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
        applicationEventPublisher.publishEvent(
                new NotificationDto(
                        new AuthorDto(requestor.email()),
                        NotificationDto.NotificationType.NEW_LINK,
                        "Connect items",
                        linkEntity.getUuid()
                )
        );
        return Optional.of(linkEntity);
    }

    @Override
    public Optional<LinkDto> disconnectEntities(LinkDto linkDto, UserDetail requestor) {
        LinkDto linkEntity = this.linkRepository.findFirstBySourceItemUuidAndTargetItemUuidAndLinkTypeLabel(linkDto.sourceItemId(), linkDto.targetItemId(), linkDto.linkType().label())
                .map(LinkMapper.INSTANCE::toDto)
                .orElseThrow(LinkedItemDoesNotExist::new);
        this.linkRepository.deleteAllBySourceItemUuidOrTargetItemUuid(linkDto.sourceItemId(), linkDto.targetItemId());
        applicationEventPublisher.publishEvent(
                new NotificationDto(
                        new AuthorDto(requestor.email()),
                        NotificationDto.NotificationType.DELETE_LINK,
                        "Disconnect items",
                        linkEntity
                )
        );
        return Optional.of(linkEntity);
    }

}
