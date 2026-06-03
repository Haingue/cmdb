package com.management.cmdb.services.inventory.service;

import com.management.cmdb.services.inventory.dto.LinkDto;
import com.management.cmdb.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.services.inventory.dto.NotificationDto;
import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import com.management.cmdb.services.inventory.entity.LinkTypeEntity;
import com.management.cmdb.services.inventory.exception.LinkedItemDoesNotExist;
import com.management.cmdb.services.inventory.model.UserDetail;
import com.management.cmdb.services.inventory.repository.ItemRepository;
import com.management.cmdb.services.inventory.repository.LinkRepository;
import com.management.cmdb.services.inventory.repository.LinkTypeRepository;
import com.management.cmdb.services.inventory.service.impl.LinkServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LinkServiceTest {


    @Mock
    private ItemRepository  itemRepository;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private LinkTypeRepository linkTypeRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private LinkServiceImpl linkService;

    @Captor
    private ArgumentCaptor<NotificationDto> notificationCaptor;

    private ItemEntity sourceItem;
    private ItemEntity targetItem;
    private LinkTypeEntity linkType;
    private UserDetail requestor;
    private LinkDto linkDto;

    @BeforeEach
    void setUp() {
        sourceItem = new ItemEntity();
        sourceItem.setUuid(UUID.randomUUID());

        targetItem = new ItemEntity();
        targetItem.setUuid(UUID.randomUUID());

        linkType = new LinkTypeEntity();
        linkType.setLabel("CONNECTED_TO");

        requestor = new UserDetail(UUID.randomUUID(), "Test User", "test@example.com");

        linkDto = new LinkDto(
                new LinkTypeDto(UUID.randomUUID(), "CONNECTED_TO"),
                sourceItem.getUuid(),
                sourceItem.getName(),
                targetItem.getUuid(),
                targetItem.getName(),
                "Test link");
    }

    @Test
    void connectEntities_shouldCreateNewLink_whenLinkDoesNotExist() {
        given(linkTypeRepository.findFirstByLabelIgnoreCase("CONNECTED_TO"))
                .willReturn(Optional.of(linkType));
        given(linkRepository.findFirstBySourceItemUuidAndTargetItemUuidAndLinkTypeLabel(
                sourceItem.getUuid(), targetItem.getUuid(), "CONNECTED_TO"))
                .willReturn(Optional.empty());
        given(linkRepository.save(any(LinkEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(itemRepository.findById(sourceItem.getUuid()))
                .willReturn(Optional.of(sourceItem));
        given(itemRepository.findById(targetItem.getUuid()))
                .willReturn(Optional.of(targetItem));

        Optional<LinkDto> result = linkService.connectEntities(linkDto, requestor);

        assertThat(result).isPresent();
        then(linkRepository).should().save(any(LinkEntity.class));
        then(applicationEventPublisher).should().publishEvent(notificationCaptor.capture());
        NotificationDto notification = notificationCaptor.getValue();
        assertThat(notification.type()).isEqualTo(NotificationDto.NotificationType.NEW_LINK);
        assertThat(notification.author().email()).isEqualTo(requestor.email());
    }

    @Test
    void connectEntities_shouldUpdateExistingLink_whenLinkExists() {
        LinkEntity existingLink = new LinkEntity();
        existingLink.setUuid(UUID.randomUUID());
        existingLink.setSourceItem(sourceItem);
        existingLink.setTargetItem(targetItem);
        existingLink.setLinkType(linkType);
        existingLink.setDescription("Old description");

        given(linkTypeRepository.findFirstByLabelIgnoreCase("CONNECTED_TO"))
                .willReturn(Optional.of(linkType));
        given(linkRepository.findFirstBySourceItemUuidAndTargetItemUuidAndLinkTypeLabel(
                sourceItem.getUuid(), targetItem.getUuid(), "CONNECTED_TO"))
                .willReturn(Optional.of(existingLink));
        given(linkRepository.save(any(LinkEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(itemRepository.findById(sourceItem.getUuid()))
                .willReturn(Optional.of(sourceItem));
        given(itemRepository.findById(targetItem.getUuid()))
                .willReturn(Optional.of(targetItem));

        Optional<LinkDto> result = linkService.connectEntities(linkDto, requestor);

        assertThat(result).isPresent();
        assertThat(result.get().description()).isEqualTo("Test link");
        then(linkRepository).should().save(any(LinkEntity.class));
        then(applicationEventPublisher).should().publishEvent(any(NotificationDto.class));
    }

    @Test
    void connectEntities_shouldUseUndefinedLinkType_whenLinkTypeNotFound() {
        given(linkTypeRepository.findFirstByLabelIgnoreCase("CONNECTED_TO"))
                .willReturn(Optional.empty());
        given(linkRepository.findFirstBySourceItemUuidAndTargetItemUuidAndLinkTypeLabel(
                sourceItem.getUuid(), targetItem.getUuid(), "UNDEFINED"))
                .willReturn(Optional.empty());
        given(linkRepository.save(any(LinkEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(itemRepository.findById(sourceItem.getUuid()))
                .willReturn(Optional.of(sourceItem));
        given(itemRepository.findById(targetItem.getUuid()))
                .willReturn(Optional.of(targetItem));

        Optional<LinkDto> result = linkService.connectEntities(linkDto, requestor);

        assertThat(result).isPresent();
        then(linkRepository).should().save(any(LinkEntity.class));
    }

    @Test
    void disconnectEntities_shouldDeleteLink_whenLinkExists() {
        LinkEntity existingLink = new LinkEntity();
        existingLink.setUuid(UUID.randomUUID());
        existingLink.setSourceItem(sourceItem);
        existingLink.setTargetItem(targetItem);
        existingLink.setLinkType(linkType);

        given(linkRepository.findFirstBySourceItemUuidAndTargetItemUuidAndLinkTypeLabel(
                sourceItem.getUuid(), targetItem.getUuid(), "CONNECTED_TO"))
                .willReturn(Optional.of(existingLink));

        Optional<LinkDto> result = linkService.disconnectEntities(linkDto, requestor);

        assertThat(result).isPresent();
        then(linkRepository).should().deleteAllBySourceItemUuidOrTargetItemUuid(
                sourceItem.getUuid(), targetItem.getUuid());
        then(applicationEventPublisher).should().publishEvent(notificationCaptor.capture());
        NotificationDto notification = notificationCaptor.getValue();
        assertThat(notification.type()).isEqualTo(NotificationDto.NotificationType.DELETE_LINK);
    }

    @Test
    void disconnectEntities_shouldThrowException_whenLinkDoesNotExist() {
        given(linkRepository.findFirstBySourceItemUuidAndTargetItemUuidAndLinkTypeLabel(
                sourceItem.getUuid(), targetItem.getUuid(), "CONNECTED_TO"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> linkService.disconnectEntities(linkDto, requestor))
                .isInstanceOf(LinkedItemDoesNotExist.class);
        then(linkRepository).should(never()).deleteAllBySourceItemUuidOrTargetItemUuid(any(), any());
        then(applicationEventPublisher).should(never()).publishEvent(any());
    }


}