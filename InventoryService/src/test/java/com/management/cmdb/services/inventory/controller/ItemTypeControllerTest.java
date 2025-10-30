package com.management.cmdb.services.inventory.controller;

import com.management.cmdb.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.services.inventory.entity.ItemTypeEntity;
import com.management.cmdb.services.inventory.mapper.ItemTypeMapper;
import com.management.cmdb.services.inventory.exemple.ItemTypeExample;
import com.management.cmdb.services.inventory.repository.ItemTypeRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemTypeControllerTest {

    @Resource
    private WebTestClient webTestClient;
    @Resource
    private ItemTypeRepository itemTypeRepository;

    @Test
    void postItemType() {
        ItemTypeEntity modifiedItemType = ItemTypeMapper.INSTANCE.toEntity(ItemTypeMapper.INSTANCE.toDto(ItemTypeExample.HOST.itemType));
        modifiedItemType.setLabel("COPY_HOST");
        modifiedItemType.setUuid(null);
        Mono<ItemTypeDto> dto = Mono.just(ItemTypeMapper.INSTANCE.toDto(modifiedItemType));
        ItemTypeDto itemDto = webTestClient.post()
                .uri("/item-type")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto, ItemTypeDto.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ItemTypeDto.class)
                .getResponseBody().blockFirst();
        Assertions.assertNotNull(itemDto);
        Assertions.assertNotNull(itemDto.uuid());
        Optional<ItemTypeEntity> example = itemTypeRepository.findById(itemDto.uuid());
        Assertions.assertNotNull(example);
        Assertions.assertTrue(example.isPresent());
        Assertions.assertEquals(modifiedItemType.getLabel(), example.get().getLabel());
        Assertions.assertEquals(modifiedItemType.getDescription(), example.get().getDescription());

        itemTypeRepository.deleteById(itemDto.uuid());
    }

    @Test
    void getItemById() {
        final ItemTypeDto existingItemType = ItemTypeMapper.INSTANCE.toDto(ItemTypeExample.HOST.itemType);

        ItemTypeDto result = webTestClient.get()
                .uri("/item-type/" + existingItemType.uuid())
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ItemTypeDto.class)
                .getResponseBody()
                .blockFirst();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingItemType, result);
        Assertions.assertEquals(existingItemType.label(), result.label());
        Assertions.assertEquals(existingItemType.description(), result.description());
    }
}