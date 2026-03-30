package com.management.cmdb.services.inventory.controller;

import com.management.cmdb.services.inventory.dto.LinkDto;
import com.management.cmdb.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import com.management.cmdb.services.inventory.entity.LinkTypeEntity;
import com.management.cmdb.services.inventory.entity.PredefinedLinkType;
import com.management.cmdb.services.inventory.job.StartupJob;
import com.management.cmdb.services.inventory.mapper.LinkTypeMapper;
import com.management.cmdb.services.inventory.repository.ItemRepository;
import com.management.cmdb.services.inventory.repository.LinkRepository;
import com.management.cmdb.services.inventory.repository.LinkTypeRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LinkControllerTest {

    @Resource
    private WebTestClient webTestClient;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private LinkTypeRepository linkTypeRepository;

    @Autowired
    private ItemRepository itemRepository;

    private LinkDto validLinkDto;
    private ItemEntity sourceItem;
    private ItemEntity targetItem;
    private LinkTypeEntity linkType;

    @BeforeEach
    void setUp() {
        sourceItem = new ItemEntity();
        sourceItem.setUuid(UUID.randomUUID());
        sourceItem.setName("Source Item");
        itemRepository.save(sourceItem);

        targetItem = new ItemEntity();
        targetItem.setUuid(UUID.randomUUID());
        targetItem.setName("Target Item");
        itemRepository.save(targetItem);

        linkType = new LinkTypeEntity();
        linkType.setUuid(UUID.randomUUID());
        linkType.setLabel("CONNECTED_TO");
        linkTypeRepository.save(linkType);

        validLinkDto = new LinkDto(
                LinkTypeMapper.INSTANCE.toDto(linkType),
                sourceItem.getUuid(),
                sourceItem.getName(),
                targetItem.getUuid(),
                targetItem.getName(),
                "Test link description"
        );
    }

    @Test
    void createLink_shouldReturnCreatedLink_whenValidRequest() {
        webTestClient.post()
                .uri("/link")
                .body(BodyInserters.fromValue(validLinkDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkDto.class)
                .value(linkDto -> {
                    assertThat(linkDto.sourceItemId()).isEqualTo(validLinkDto.sourceItemId());
                    assertThat(linkDto.targetItemId()).isEqualTo(validLinkDto.targetItemId());
                    assertThat(linkDto.linkType().label()).isEqualTo("CONNECTED_TO");
                });
    }

    @Test
    void createLink_shouldReturnBadRequest_whenLinkTypeDoesNotExist() {
        LinkDto invalidLinkDto = new LinkDto(
                new LinkTypeDto(UUID.randomUUID(), "UNKNOWN_TYPE"),
                sourceItem.getUuid(),
                sourceItem.getName(),
                targetItem.getUuid(),
                targetItem.getName(),
                "Test link description"
        );

        webTestClient.post()
                .uri("/link")
                .body(BodyInserters.fromValue(invalidLinkDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkDto.class)
                .value(linkDto -> {
                    assertThat(linkDto.sourceItemId()).isEqualTo(validLinkDto.sourceItemId());
                    assertThat(linkDto.targetItemId()).isEqualTo(validLinkDto.targetItemId());
                    assertThat(linkDto.linkType().label()).isEqualTo(PredefinedLinkType.UNDEFINED_LINK_TYPE.getLabel());
                });
    }
    @Test
    void createLink_shouldReturnBadRequest_whenItemDoesNotExist() {
        LinkDto invalidLinkDto = new LinkDto(
                LinkTypeMapper.INSTANCE.toDto(linkType),
                UUID.randomUUID(), // non-existing item UUID
                "Unknown Source",
                UUID.randomUUID(), // non-existing item UUID
                "Unknown Target",
                "Test link description"
        );

        webTestClient.post()
                .uri("/link")
                .body(BodyInserters.fromValue(invalidLinkDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void deleteLink_shouldReturnDeletedLink_whenLinkExists() {
        LinkEntity existingLink = new LinkEntity();
        existingLink.setUuid(UUID.randomUUID());
        existingLink.setSourceItem(sourceItem);
        existingLink.setTargetItem(targetItem);
        existingLink.setLinkType(linkType);
        existingLink.setDescription("Existing link");
        linkRepository.save(existingLink);

        LinkDto existingLinkDto = new LinkDto(
                LinkTypeMapper.INSTANCE.toDto(linkType),
                sourceItem.getUuid(),
                sourceItem.getName(),
                targetItem.getUuid(),
                targetItem.getName(),
                "Existing link"
        );

        webTestClient
                .method(HttpMethod.DELETE)
                .uri("/link")
                .body(BodyInserters.fromValue(existingLinkDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkDto.class)
                .value(linkDto -> {
                    assertThat(linkDto.sourceItemId()).isEqualTo(existingLinkDto.sourceItemId());
                    assertThat(linkDto.targetItemId()).isEqualTo(existingLinkDto.targetItemId());
                });
    }

    @Test
    void deleteLink_shouldReturnBadRequest_whenLinkDoesNotExist() {
        LinkDto nonExistentLinkDto = new LinkDto(
                LinkTypeMapper.INSTANCE.toDto(linkType),
                sourceItem.getUuid(),
                sourceItem.getName(),
                targetItem.getUuid(),
                targetItem.getName(),
                "Non-existent link"
        );

        webTestClient
                .method(HttpMethod.DELETE)
                .uri("/link")
                .body(BodyInserters.fromValue(nonExistentLinkDto))
                .exchange()
                .expectStatus().isBadRequest();
    }



}