package com.management.cmdb.services.inventory.service.impl;

import com.management.cmdb.services.inventory.dto.AuthorDto;
import com.management.cmdb.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.services.inventory.dto.NotificationDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.inventory.entity.LinkTypeEntity;
import com.management.cmdb.services.inventory.exception.LinkTypeExist;
import com.management.cmdb.services.inventory.exception.LinkTypeNotValid;
import com.management.cmdb.services.inventory.mapper.LinkTypeMapper;
import com.management.cmdb.services.inventory.model.UserDetail;
import com.management.cmdb.services.inventory.repository.LinkTypeRepository;
import com.management.cmdb.services.inventory.service.LinkTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LinkTypeServiceImpl implements LinkTypeService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final LinkTypeRepository linkTypeRepository;

    public LinkTypeServiceImpl(ApplicationEventPublisher applicationEventPublisher, LinkTypeRepository linkTypeRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.linkTypeRepository = linkTypeRepository;
    }

    @Override
    public LinkTypeDto create(LinkTypeDto linkTypeDto, UserDetail author) {
        if (StringUtils.isBlank(linkTypeDto.label())) throw new LinkTypeNotValid();
        if (this.linkTypeRepository.findFirstByLabel(linkTypeDto.label()).isPresent()) throw new LinkTypeExist();

        LinkTypeEntity newLinkType = LinkTypeMapper.INSTANCE.toEntity(linkTypeDto);
        newLinkType.setUuid(UUID.randomUUID());
        newLinkType = this.linkTypeRepository.save(newLinkType);

        applicationEventPublisher.publishEvent(
                new NotificationDto(
                        new AuthorDto(author.email()),
                        NotificationDto.NotificationType.NEW_LINK_TYPE,
                        "Create new link type",
                        newLinkType.getUuid()
                )
        );
        return LinkTypeMapper.INSTANCE.toDto(newLinkType);
    }

    @Override
    public Optional<LinkTypeDto> findById(UUID uuid) {
        return this.linkTypeRepository
                .findById(uuid)
                .map(LinkTypeMapper.INSTANCE::toDto);
    }

    @Override
    public Optional<LinkTypeDto> findByLabel(String label) {
        return this.linkTypeRepository
                .findFirstByLabel(label)
                .map(LinkTypeMapper.INSTANCE::toDto);
    }

    @Override
    public PaginatedResponseDto<LinkTypeDto> search(String label, int pageNumber, int pageSize) {
        return PaginatedResponseDto.toPaginatedDto(this.linkTypeRepository
                .searchAllByLabelLikeIgnoreCase(label, PageRequest.of(pageNumber, pageSize)), LinkTypeMapper.INSTANCE::toDto);
    }
}
