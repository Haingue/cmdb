package com.management.cmdb.services.inventory.controller;

import com.management.cmdb.services.inventory.dto.ItemDto;
import com.management.cmdb.services.inventory.entity.AttributeEntity;
import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import com.management.cmdb.services.inventory.exemple.ItemExample;
import com.management.cmdb.services.inventory.exemple.LinkTypeExample;
import com.management.cmdb.services.inventory.mapper.ItemMapper;
import com.management.cmdb.services.inventory.repository.ItemRepository;
import com.management.cmdb.services.inventory.repository.LinkTypeRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemControllerTest {

    @Resource
    private WebTestClient webTestClient;
    @Resource
    private LinkTypeRepository linkTypeRepository;
    @Resource
    private ItemRepository itemRepository;

    @Test
    @Order(1)
    void postItem() {
        ItemEntity originalItem = ItemExample.JETTY01.toEntity();
        originalItem.setName("New jetty server");
        ItemDto newItem = ItemMapper.INSTANCE.toDto(originalItem);
        Mono<ItemDto> dto = Mono.just(newItem);
        ItemDto itemDto = webTestClient.post()
                .uri("/item")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto, ItemDto.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ItemDto.class)
                .getResponseBody().blockFirst();
        Assertions.assertNotNull(itemDto);
        Assertions.assertNotNull(itemDto.uuid());
        Optional<ItemEntity> example = itemRepository.findById(itemDto.uuid());
        Assertions.assertNotNull(example);
        Assertions.assertTrue(example.isPresent());
        Assertions.assertEquals(newItem.name(), example.get().getName());
        Assertions.assertEquals(newItem.description(), example.get().getDescription());
        Optional<AttributeEntity> firstAttribute = example.get().getAttributes().stream().findFirst();
        Assertions.assertNotNull(firstAttribute);
        Assertions.assertTrue(firstAttribute.isPresent());
        Assertions.assertNotNull(firstAttribute.get().getValueStr());
    }

    @Test
    @Order(2)
    void postItemWithOutgoingLink() {
        ItemEntity newItem = ItemMapper.INSTANCE.toEntity(ItemMapper.INSTANCE.toDto(ItemExample.JETTY01.toEntity()));
        newItem.setUuid(null);
        newItem.setName("New jetty server outgoing link");
        ItemEntity pgItem = ItemExample.POSTGRESQL01.toEntity();

        LinkEntity jettyExchangeWithPg = new LinkEntity();
        jettyExchangeWithPg.setUuid(null);
        jettyExchangeWithPg.setLinkType(LinkTypeExample.COMMUNICATE_WITH.toEntity());
        jettyExchangeWithPg.setTargetItem(pgItem);
        //jettyExchangeWithPg.setSourceItem(newItem);
        newItem.getOutgoingLinks().add(jettyExchangeWithPg);

        Mono<ItemDto> dto = Mono.just(ItemMapper.INSTANCE.toDto(newItem));
        ItemDto itemDto = webTestClient.post()
                .uri("/item")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto, ItemDto.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ItemDto.class)
                .getResponseBody().blockFirst();

        Assertions.assertNotNull(itemDto);
        Assertions.assertNotNull(itemDto.uuid());
        Optional<ItemEntity> example = itemRepository.findById(itemDto.uuid());
        Assertions.assertNotNull(example);
        Assertions.assertTrue(example.isPresent());
        Assertions.assertEquals(newItem.getName(), example.get().getName());
        Assertions.assertEquals(newItem.getDescription(), example.get().getDescription());

        Assertions.assertFalse(example.get().getOutgoingLinks().isEmpty());
        Optional<LinkEntity> firstLink = example.get().getOutgoingLinks().stream().findFirst();
        Assertions.assertNotNull(firstLink);
        Assertions.assertTrue(firstLink.isPresent());
        Assertions.assertEquals(pgItem.getUuid(), firstLink.get().getTargetItem().getUuid());
    }
    @Test
    @Order(2)
    void postItemWithIncomingLink() {
        ItemEntity newItem = ItemMapper.INSTANCE.toEntity(ItemMapper.INSTANCE.toDto(ItemExample.JETTY01.toEntity()));
        newItem.setUuid(null);
        newItem.setName("New jetty server incoming link");
        ItemEntity pgItem = ItemExample.POSTGRESQL01.toEntity();

        LinkEntity jettyExchangeWithPg = new LinkEntity();
        jettyExchangeWithPg.setUuid(null);
        jettyExchangeWithPg.setLinkType(LinkTypeExample.COMMUNICATE_WITH.toEntity());
        //jettyExchangeWithPg.setTargetItem(newItem);
        jettyExchangeWithPg.setSourceItem(pgItem);
        newItem.getIncomingLinks().add(jettyExchangeWithPg);

        Mono<ItemDto> dto = Mono.just(ItemMapper.INSTANCE.toDto(newItem));
        ItemDto itemDto = webTestClient.post()
                .uri("/item")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto, ItemDto.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ItemDto.class)
                .getResponseBody().blockFirst();

        Assertions.assertNotNull(itemDto);
        Assertions.assertNotNull(itemDto.uuid());
        Optional<ItemEntity> example = itemRepository.findById(itemDto.uuid());
        Assertions.assertNotNull(example);
        Assertions.assertTrue(example.isPresent());
        Assertions.assertEquals(newItem.getName(), example.get().getName());
        Assertions.assertEquals(newItem.getDescription(), example.get().getDescription());

        Assertions.assertFalse(example.get().getIncomingLinks().isEmpty());
        Optional<LinkEntity> firstLink = example.get().getIncomingLinks().stream().findFirst();
        Assertions.assertNotNull(firstLink);
        Assertions.assertTrue(firstLink.isPresent());
        Assertions.assertEquals(pgItem.getUuid(), firstLink.get().getSourceItem().getUuid());
        Assertions.assertEquals(itemDto.uuid(), firstLink.get().getTargetItem().getUuid());
    }

    @Test
    @Order(4)
    void putItem() {
        ItemEntity existingItem = itemRepository.save(ItemExample.JETTY01.toEntity());
        existingItem.setDescription("New description");
        Mono<ItemDto> dto = Mono.just(ItemMapper.INSTANCE.toDto(existingItem));

        webTestClient.put()
                .uri("/item")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto, ItemDto.class)
                .exchange()
                .expectStatus()
                .isOk();

        Optional<ItemEntity> example = itemRepository.findById(ItemExample.JETTY01.toDto().uuid());
        Assertions.assertNotNull(example);
        Assertions.assertTrue(example.isPresent());
        Assertions.assertEquals(existingItem.getName(), example.get().getName());
        Assertions.assertEquals(existingItem.getDescription(), example.get().getDescription());
    }

    @Test
    @Order(5)
    void getItemById() {
        ItemEntity existingItem = itemRepository.save(ItemExample.JETTY01.toEntity());

        ItemDto result = webTestClient.get()
                .uri("/item/" + existingItem.getUuid())
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ItemDto.class)
                .getResponseBody()
                .blockFirst();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(ItemExample.JETTY01.toDto(), result);
        Assertions.assertEquals(ItemExample.JETTY01.toDto().name(), result.name());
        Assertions.assertEquals(ItemExample.JETTY01.toDto().description(), result.description());
    }

    @Test
    @Order(6)
    void deleteItem() {
        ItemEntity existingItem = itemRepository.save(ItemExample.JETTY01.toEntity());
        webTestClient.method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder.path("/item/{uuid}").build(existingItem.getUuid()))
                .exchange()
                .expectStatus()
                .isOk();

        Optional<ItemEntity> example = itemRepository.findById(ItemExample.JETTY01.toDto().uuid());
        Assertions.assertTrue(example.isEmpty());
    }
}