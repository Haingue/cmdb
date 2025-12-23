package com.management.cmdb.services.aggregator.syslog.controller;

import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import com.management.cmdb.services.aggregator.syslog.model.event.NewFocusedTrafficEvent;
import com.management.cmdb.services.aggregator.syslog.model.event.NewTrafficEvent;
import com.management.cmdb.services.aggregator.syslog.model.event.TrafficEvent;
import com.management.cmdb.services.aggregator.syslog.service.NotificationService;
import com.management.cmdb.services.aggregator.syslog.service.SyslogService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/traffic")
public class TrafficController {

    private final SyslogService syslogService;
    private final NotificationService notificationService;

    public TrafficController(SyslogService syslogService, NotificationService notificationService) {
        this.syslogService = syslogService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Map<String, Traffic>>> getTraffic() {
        return ResponseEntity.ok(syslogService.getTraffics());
    }

    @GetMapping(path = "/stream/focused", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<TrafficEvent>> getTrafficStream() {
        return Flux.create(sink -> {
            sink.next(ServerSentEvent.<TrafficEvent>builder().event("syslog").build());
            Disposable subscription = notificationService.getSyslog().asFlux()
                    .filter(event -> event.event() != null
                            && event.event().equals("FOCUSED_TRAFFIC") && event.data() != null)
                    .subscribe(sink::next);
            sink.onCancel(subscription);
        });
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<TrafficEvent>> getTrafficStream(@RequestParam(required = false) String source, @RequestParam(required = false) String destination) {
        return Flux.create(sink -> {
            sink.next(ServerSentEvent.<TrafficEvent>builder().event("syslog").build());
            Disposable subscription = notificationService.getSyslog().asFlux()
                    .filter(event -> event.data() != null
                            && (source == null || event.data().getSourceIp().equals(source))
                            && (destination == null || event.data().getDestinationIp().equals(destination)))
                    .subscribe(sink::next);
            sink.onCancel(subscription);
        });
    }
}
