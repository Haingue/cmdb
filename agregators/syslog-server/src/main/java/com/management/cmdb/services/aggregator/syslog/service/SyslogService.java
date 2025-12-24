package com.management.cmdb.services.aggregator.syslog.service;

import com.management.cmdb.services.aggregator.syslog.configuration.SyslogServerProperties;
import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import com.management.cmdb.services.aggregator.syslog.model.event.NewFocusedTrafficEvent;
import com.management.cmdb.services.aggregator.syslog.model.event.NewTrafficEvent;
import com.management.cmdb.services.aggregator.syslog.repository.TrafficRepository;
import com.management.cmdb.services.aggregator.syslog.server.PaloAltoMessageParser;
import com.management.cmdb.services.aggregator.syslog.server.PaloAltoSyslogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SyslogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogService.class);


    private final SyslogServerProperties syslogServerProperties;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TrafficRepository trafficRepository;

    public SyslogService(SyslogServerProperties syslogServerProperties, ApplicationEventPublisher applicationEventPublisher, TrafficRepository trafficRepository) {
        this.syslogServerProperties = syslogServerProperties;
        this.applicationEventPublisher = applicationEventPublisher;
        this.trafficRepository = trafficRepository;
    }

    public void syslogAnalysis(String message) {
        PaloAltoSyslogMessage parsedMessage = PaloAltoMessageParser.parse(message);

        if (parsedMessage.getType().equals("TRAFFIC") && parsedMessage.getAction().equals("allow")) {

            if (isIgnoredHost(parsedMessage)) {
                applicationEventPublisher.publishEvent(NewTrafficEvent.builder()
                        .sourceIp(parsedMessage.getSrc())
                        .destinationIp(parsedMessage.getDst())
                        .protocol(parsedMessage.getProto())
                        .build());
                return;
            }
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
            applicationEventPublisher.publishEvent(NewFocusedTrafficEvent.builder()
                    .sourceIp(parsedMessage.getSrc())
                    .destinationIp(parsedMessage.getDst())
                    .protocol(parsedMessage.getProto())
                    .build());
            trafficRepository.save(parsedMessage.getSrc(), parsedMessage.getDst(), oneTraffic);
        }
    }

    private boolean isIgnoredHost(PaloAltoSyslogMessage traffic) {
        for (String host : syslogServerProperties.getFocusedHost()) {
            if (traffic.getSrc().equalsIgnoreCase(host) || traffic.getDst().equalsIgnoreCase(host)) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Map<String, Traffic>> getTraffics() {
        return trafficRepository.getTraffics();
    }

}
