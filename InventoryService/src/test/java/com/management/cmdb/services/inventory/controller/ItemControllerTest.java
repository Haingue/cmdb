package com.management.cmdb.services.inventory.controller;

import com.management.cmdb.services.inventory.dto.ItemDto;
import com.management.cmdb.services.inventory.dto.LinkDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
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
        Assertions.assertEquals(newItem.getName(), itemDto.name());
        Assertions.assertEquals(newItem.getDescription(), itemDto.description());

        Assertions.assertFalse(itemDto.outgoingLinks().isEmpty());
        Optional<LinkDto> firstLink = itemDto.outgoingLinks().stream().findFirst();
        Assertions.assertNotNull(firstLink);
        Assertions.assertTrue(firstLink.isPresent());
        Assertions.assertEquals(pgItem.getUuid(), firstLink.get().targetItemId());
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
        Assertions.assertEquals(newItem.getName(), itemDto.name());
        Assertions.assertEquals(newItem.getDescription(), itemDto.description());

        Assertions.assertFalse(itemDto.incomingLinks().isEmpty());
        Optional<LinkDto> firstLink = itemDto.incomingLinks().stream().findFirst();
        Assertions.assertNotNull(firstLink);
        Assertions.assertTrue(firstLink.isPresent());
        Assertions.assertEquals(pgItem.getUuid(), firstLink.get().sourceItemId());
        Assertions.assertEquals(itemDto.uuid(), firstLink.get().targetItemId());
    }

    @Test
    @Order(3)
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
    @Order(4)
    void putItemWithExistingLink() {
        ItemEntity existingItem = itemRepository.save(ItemExample.JETTY01.toEntity());

        existingItem.setDescription("New description");
        ItemEntity pgItem = ItemExample.POSTGRESQL01.toEntity();

        LinkEntity jettyExchangeWithPg = new LinkEntity();
        jettyExchangeWithPg.setUuid(null);
        jettyExchangeWithPg.setDescription("1st  link description");
        jettyExchangeWithPg.setLinkType(LinkTypeExample.COMMUNICATE_WITH.toEntity());
        jettyExchangeWithPg.setSourceItem(pgItem);
        jettyExchangeWithPg.setTargetItem(existingItem);
        existingItem.getIncomingLinks().add(jettyExchangeWithPg);

        Mono<ItemDto> dto = Mono.just(ItemMapper.INSTANCE.toDto(existingItem));
        Flux<ItemDto> itemDtoFluxExchangeResult = webTestClient.put()
                .uri("/item")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto, ItemDto.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ItemDto.class)
                .getResponseBody();
        ItemDto resultDto = itemDtoFluxExchangeResult.blockLast();

        Assertions.assertNotNull(resultDto);
        Assertions.assertEquals(existingItem.getName(), resultDto.name());
        Assertions.assertEquals(existingItem.getDescription(), resultDto.description());
        Assertions.assertEquals(1, resultDto.incomingLinks().size());

        existingItem = ItemMapper.INSTANCE.toEntity(resultDto);
        Assertions.assertEquals(1, existingItem.getIncomingLinks().size());
        ItemEntity pgItem2 = ItemExample.POSTGRESQL02.toEntity();
        LinkEntity jettyExchangeWithPg2 = new LinkEntity();
        jettyExchangeWithPg2.setUuid(null);
        jettyExchangeWithPg2.setDescription("2nd link description");
        jettyExchangeWithPg2.setLinkType(LinkTypeExample.COMMUNICATE_WITH.toEntity());
        jettyExchangeWithPg2.setSourceItem(pgItem2);
        jettyExchangeWithPg2.setTargetItem(existingItem);
        existingItem.getIncomingLinks().add(jettyExchangeWithPg2);
        Assertions.assertEquals(2, existingItem.getIncomingLinks().size());

        dto = Mono.just(ItemMapper.INSTANCE.toDto(existingItem));
        Flux<ItemDto> responseBody = webTestClient.put()
                .uri("/item")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto, ItemDto.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ItemDto.class)
                .getResponseBody();
        resultDto = responseBody.blockLast();
        Assertions.assertNotNull(resultDto);
        Assertions.assertEquals(existingItem.getName(), resultDto.name());
        Assertions.assertEquals(existingItem.getDescription(), resultDto.description());
        Assertions.assertEquals(2, resultDto.incomingLinks().size());
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
    void getItemByAttribute() {
        ItemEntity existingItem = itemRepository.save(ItemExample.JETTY01.toEntity());
        String hostname = existingItem.getAttributes().stream()
                .filter(attributeEntity -> attributeEntity.getAttributeType().getLabel().equals("hostname"))
                .findFirst().get().getValueStr();

        PaginatedResponseDto<ItemDto> result = webTestClient.get()
                .uri("/item/any/hostname/"+hostname)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(new ParameterizedTypeReference<PaginatedResponseDto<ItemDto>>() {})
                .getResponseBody()
                .blockFirst();

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        ItemDto itemResult = result.content().getFirst();
        Assertions.assertEquals(ItemExample.JETTY01.toDto(), itemResult);
        Assertions.assertEquals(ItemExample.JETTY01.toDto().name(), itemResult.name());
        Assertions.assertTrue(ItemExample.JETTY01.toDto().attributes().stream().anyMatch(attr -> attr.label().equals("hostname") && attr.value().equals("MYSERVERJETTY01")));
        Assertions.assertEquals(ItemExample.JETTY01.toDto().description(), itemResult.description());
    }

    @Test
    @Order(100)
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