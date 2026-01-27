package com.management.cmdb.services.inventory.service;

import com.management.cmdb.services.inventory.dto.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    public final Sinks.Many<ServerSentEvent<NotificationDto>> itemNotificationSink;

    public NotificationService() {
        this.itemNotificationSink = Sinks.many().multicast().directBestEffort();
    }

    public Sinks.Many<ServerSentEvent<NotificationDto>> getItemNotificationSink() {
        return itemNotificationSink;
    }

    @EventListener
    public void notificationEvent (NotificationDto notificationDto) {
        LOGGER.debug("Received notification: {}", notificationDto);
        itemNotificationSink.tryEmitNext(
                ServerSentEvent.<NotificationDto>builder()
                        .id(UUID.randomUUID().toString())
                        .event(notificationDto.type().name())
                        .comment(notificationDto.message())
                        .data(notificationDto)
                        .build()
        );
    }

}
