package com.management.cmdb.inventory.service.controller;

import com.management.cmdb.inventory.service.dto.ItemDto;
import com.management.cmdb.inventory.service.dto.NotificationDto;
import com.management.cmdb.inventory.service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@RestController
public class NotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(path = "/notification/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationDto>> subscribeToNotifications() {
        LOGGER.info("Subscribing toItemId global notifications");
        return Flux.create(sink -> {
            sink.next(ServerSentEvent.<NotificationDto>builder().event("item").build());
            Disposable subscription = notificationService.getItemNotificationSink().asFlux()
                    .subscribe(sink::next);
            sink.onCancel(subscription);
        });
    }

    @GetMapping(path = "/notification/item/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ItemDto>> subscribeToItemEvent() {
        LOGGER.info("Subscribing toItemId item notifications");
        return Flux.create(sink -> {
            sink.next(ServerSentEvent.<ItemDto>builder().event("item").build());
            Disposable subscription = notificationService.itemNotificationSink.asFlux()
                    .filter(notificationDtoServerSentEvent ->
                            Arrays.asList(
                                    NotificationDto.NotificationType.NEW_ITEM.name(),
                                    NotificationDto.NotificationType.UPDATE_ITEM.name(),
                                    NotificationDto.NotificationType.DELETE_ITEM.name())
                            .contains(notificationDtoServerSentEvent.event()))
                    .map(notificationDtoServerSentEvent -> {
                        return ServerSentEvent.<ItemDto>builder()
                                .id(notificationDtoServerSentEvent.id())
                                .event(notificationDtoServerSentEvent.event())
                                .comment(notificationDtoServerSentEvent.comment())
                                .data((ItemDto) notificationDtoServerSentEvent.data().content())
                                .build();
                    })
                    .doOnNext(itemDtoServerSentEvent -> {
                        LOGGER.debug("Received ItemDto: {}", itemDtoServerSentEvent);
                    })
                    .subscribe(sink::next);
            sink.onCancel(subscription);
        });
    }
}
