package com.management.cmdb.services.aggregator.syslog.controller;

import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import com.management.cmdb.services.aggregator.syslog.service.SyslogService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/traffic")
public class TrafficController {

    private final SyslogService syslogService;

    public TrafficController(SyslogService syslogService) {
        this.syslogService = syslogService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Map<String, Traffic>>> getTraffic() {
        return ResponseEntity.ok(syslogService.getTraffics());
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Traffic>> getTrafficStream() {
        return Flux.create(sink -> {
            sink.next(ServerSentEvent.<Traffic>builder().event("syslog").build());
            Disposable subscription = syslogService.getSyslog().asFlux()
                    .subscribe(sink::next);
            sink.onCancel(subscription);
        });
    }
}
