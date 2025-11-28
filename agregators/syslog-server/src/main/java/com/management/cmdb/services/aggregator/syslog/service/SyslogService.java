package com.management.cmdb.services.aggregator.syslog.service;

import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import com.management.cmdb.services.aggregator.syslog.repository.TrafficRepository;
import com.management.cmdb.services.aggregator.syslog.server.PaloAltoMessageParser;
import com.management.cmdb.services.aggregator.syslog.server.PaloAltoSyslogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.jta.TransactionFactory;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;

@Service
public class SyslogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogService.class);

    public final Sinks.Many<ServerSentEvent<Traffic>> syslogSink;
    private final TrafficRepository trafficRepository;

    public SyslogService(TrafficRepository trafficRepository) {
        this.trafficRepository = trafficRepository;
        this.syslogSink = Sinks.many().multicast().directBestEffort();
    }

    public void syslogAnalysis(String message) {
        PaloAltoSyslogMessage parsedMessage = PaloAltoMessageParser.parse(message);
        if (parsedMessage.getType().equals("TRAFFIC") && parsedMessage.getAction().equals("allow")) {
            LOGGER.trace("Traffic log: {}", parsedMessage);
            Traffic oneTraffic = trafficRepository.getOneTraffic(parsedMessage.getSrc(), parsedMessage.getDst())
                    .orElseGet(() -> Traffic.builder()
                            .sourceIp(parsedMessage.getSrc())
                            .destinationIp(parsedMessage.getDst())
                            .protocol(parsedMessage.getProto())
                            .application(parsedMessage.getApp())
                            .lastCommunicationDatetime(parsedMessage.getTimeGenerated())
                            .build());
            oneTraffic.setNumber(oneTraffic.getNumber() + 1);
            oneTraffic.setLastCommunicationDatetime(parsedMessage.getTimeGenerated());
            trafficRepository.save(parsedMessage.getSrc(), parsedMessage.getDst(), oneTraffic);

            syslogSink.tryEmitNext(
                    ServerSentEvent.<Traffic>builder()
                            .id(UUID.randomUUID().toString())
                            .event(parsedMessage.getType())
                            .comment("New traffic")
                            .data(oneTraffic)
                            .build());
        }
    }

    public Map<String, Map<String, Traffic>> getTraffics() {
        return trafficRepository.getTraffics();
    }

    public Sinks.Many<ServerSentEvent<Traffic>> getSyslog() {
        return syslogSink;
    }

}
