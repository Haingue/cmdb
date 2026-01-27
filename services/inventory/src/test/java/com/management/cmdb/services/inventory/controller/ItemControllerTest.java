package com.management.cmdb.services.inventory.controller;

import com.management.cmdb.services.inventory.dto.AttributeDto;
import com.management.cmdb.services.inventory.dto.ItemDto;
import com.management.cmdb.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import com.management.cmdb.services.inventory.exemple.ItemExample;
import com.management.cmdb.services.inventory.exemple.LinkTypeExample;
import com.management.cmdb.services.inventory.mapper.ItemMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    
    public static ItemDto createItem(WebTestClient webTestClient, ItemDto itemDto) {
        return webTestClient.post()
                .uri("/item")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemDto), ItemDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ItemDto.class)
                .returnResult().getResponseBody();
    }

    public static ItemDto getItem(WebTestClient webTestClient, UUID itemId) {
        return webTestClient.get()
                .uri("/item/" + itemId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .returnResult().getResponseBody();
    }

    public static ItemDto updateItem(WebTestClient webTestClient, ItemDto itemDto) {
        return webTestClient.put()
                .uri("/item")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemDto), ItemDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .returnResult().getResponseBody();
    }

    public static void deleteItem(WebTestClient webTestClient, UUID itemId) {
        webTestClient.delete()
                .uri("/item/" + itemId)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldCreateItemSuccessfully() {
        ItemDto itemDto = ItemExample.POSTGRESQL01.toDto();

        ItemDto createdItem = createItem(webTestClient, itemDto);

        assertThat(createdItem)
                .isNotNull()
                .returns(itemDto.name(), from(ItemDto::name))
                .returns(itemDto.description(), from(ItemDto::description));

        ItemDto fetchedItem = getItem(webTestClient, createdItem.uuid());
        assertThat(fetchedItem).isNotNull();
    }

    @Test
    void shouldCreateItemWithOutgoingLinkSuccessfully() {
        ItemDto postgreSqlItem = createItem(webTestClient, ItemExample.POSTGRESQL01.toDto());

        ItemEntity itemEntity = ItemExample.JETTY01.toEntity();
        LinkEntity linkEntity = new LinkEntity();
        linkEntity.setLinkType(LinkTypeExample.COMMUNICATE_WITH.toEntity());
        linkEntity.setTargetItem(ItemMapper.INSTANCE.toEntity(postgreSqlItem));
        itemEntity.getOutgoingLinks().add(linkEntity);
        ItemDto createdItem = createItem(webTestClient, ItemMapper.INSTANCE.toDto(itemEntity));

        assertThat(createdItem)
                .isNotNull()
                .returns(itemEntity.getName(), from(ItemDto::name))
                .returns(itemEntity.getDescription(), from(ItemDto::description));

        assertThat(createdItem.outgoingLinks())
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void shouldUpdateItemSuccessfully() {
        ItemDto itemDto = ItemExample.POSTGRESQL01.toDto();
        ItemDto createdItem = createItem(webTestClient, itemDto);

        ItemEntity entity = ItemMapper.INSTANCE.toEntity(createdItem);
        entity.setDescription("New description");
        ItemDto updatedItem = updateItem(webTestClient, ItemMapper.INSTANCE.toDto(entity));

        assertThat(updatedItem)
                .isNotNull()
                .returns(entity.getDescription(), from(ItemDto::description));
    }

    @Test
    void shouldGetItemByIdSuccessfully() {
        ItemDto itemDto = ItemExample.POSTGRESQL01.toDto();
        ItemDto createdItem = createItem(webTestClient, itemDto);

        ItemDto fetchedItem = getItem(webTestClient, createdItem.uuid());

        assertThat(fetchedItem)
                .isNotNull()
                .returns(createdItem.uuid(), from(ItemDto::uuid))
                .returns(createdItem.name(), from(ItemDto::name))
                .returns(createdItem.description(), from(ItemDto::description));
        assertThat(fetchedItem.type())
                .isNotNull()
                .returns(createdItem.type().label(), from(ItemTypeDto::label))
                .returns(createdItem.type().description(), from(ItemTypeDto::description))
                .returns(createdItem.type().uuid(), from(ItemTypeDto::uuid));
    }

    @Test
    void shouldGetItemByAttributeSuccessfully() {
        ItemDto itemDto = ItemExample.JETTY01.toDto();
        ItemDto createdItem = createItem(webTestClient, itemDto);

        String hostname = createdItem.attributes().stream()
                .filter(attr -> attr.label().equalsIgnoreCase("hostname"))
                .findFirst()
                .map(AttributeDto::value)
                .orElseThrow();

        PaginatedResponseDto<ItemDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/item/any/hostname/{hostname}")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(hostname))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PaginatedResponseDto<ItemDto>>() {})
                .returnResult().getResponseBody();

        assertThat(result)
                .isNotNull()
                .matches(paginatedResponse -> !paginatedResponse.isEmpty())
                .matches(paginatedResponse -> paginatedResponse.content().size() <= 10);

        assertThat(result.content().get(0))
                .isNotNull()
                .returns(createdItem.uuid(), from(ItemDto::uuid));
    }

    @Test
    void shouldDeleteItemSuccessfully() {
        ItemDto itemDto = ItemExample.POSTGRESQL01.toDto();
        ItemDto createdItem = createItem(webTestClient, itemDto);

        assertThat(createdItem)
                .extracting(ItemDto::uuid)
                        .isNotNull();

        deleteItem(webTestClient, createdItem.uuid());

        webTestClient.get()
                .uri("/item/" + createdItem.uuid())
                .exchange()
                .expectStatus().isNotFound();
    }
}