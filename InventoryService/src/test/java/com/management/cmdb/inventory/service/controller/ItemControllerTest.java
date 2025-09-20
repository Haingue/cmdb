package com.management.cmdb.inventory.service.controller;

import com.management.cmdb.inventory.service.dto.ItemDto;
import com.management.cmdb.inventory.service.entity.ItemEntity;
import com.management.cmdb.inventory.service.exemple.ItemExample;
import com.management.cmdb.inventory.service.mapper.ItemMapper;
import com.management.cmdb.inventory.service.repository.ItemRepository;
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
    private ItemRepository itemRepository;

    @Test
    @Order(1)
    void postItem() {
        ItemEntity originalItem = ItemExample.JETTY01.toEntity();
        originalItem.setName("New jetty server");
        ItemDto newItem = ItemMapper.toDto(originalItem);
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
    }

    @Test
    @Order(2)
    void putItem() {
        ItemEntity existingItem = itemRepository.save(ItemExample.JETTY01.toEntity());
        existingItem.setDescription("New description");
        Mono<ItemDto> dto = Mono.just(ItemMapper.toDto(existingItem));

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
    @Order(3)
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
    @Order(4)
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