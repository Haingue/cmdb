package com.management.cmdb.services.aggregator.syslog.service;

import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import com.management.cmdb.services.aggregator.syslog.model.event.NewFocusedTrafficEvent;
import com.management.cmdb.services.aggregator.syslog.model.event.NewTrafficEvent;
import com.management.cmdb.services.aggregator.syslog.model.event.TrafficEvent;
import com.management.cmdb.services.aggregator.syslog.server.PaloAltoSyslogMessage;
import org.springframework.context.event.EventListener;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
public class NotificationService {
    private final Sinks.Many<ServerSentEvent<TrafficEvent>> syslogSink;

    public NotificationService() {
        this.syslogSink = Sinks.many().multicast().directBestEffort();
    }

    @EventListener
    public void onNewTrafficEvent(NewTrafficEvent traffic) {
        sendNewTrafficNotification(traffic);
    }

    @EventListener
    public void onNewFocusedTrafficEvent(NewFocusedTrafficEvent traffic) {
        sendNewFocusedTrafficNotification(traffic);
    }

public Sinks.Many<ServerSentEvent<TrafficEvent>> getSyslog() {
        return syslogSink;
    }

    public void sendNewTrafficNotification(TrafficEvent event) {
        syslogSink.tryEmitNext(
                ServerSentEvent.<TrafficEvent>builder()
                        .id(UUID.randomUUID().toString())
                        .event(event.getClass().getName())
                        .comment("TRAFFIC")
                        .data(event)
                        .build());
    }

    public void sendNewFocusedTrafficNotification(NewFocusedTrafficEvent event) {
        syslogSink.tryEmitNext(
                ServerSentEvent.<TrafficEvent>builder()
                        .id(UUID.randomUUID().toString())
                        .event(event.getClass().getName())
                        .comment("FOCUSED_TRAFFIC")
                        .data(event)
                        .build());
    }
}
