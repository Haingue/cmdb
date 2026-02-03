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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
                            .destinationPort(parsedMessage.getDport())
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
            collectMessageToText(message, oneTraffic);
        }
    }

    private void collectMessageToText (String message, Traffic traffic) {
        String exportRawMessageFilePath = syslogServerProperties.getExportRawMessageFilePath();
        if (exportRawMessageFilePath != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportRawMessageFilePath, true))) {
                writer.write(message);
                writer.newLine();
            } catch (IOException e) {
                LOGGER.error("Failed to write syslog message to text file", e);
            }
        }
        String exportMessageCsvPath = syslogServerProperties.getExportMessageCsvPath();
        if (exportMessageCsvPath != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportMessageCsvPath, true))) {
                StringBuilder csvLine = new StringBuilder();
                String csvDelimiter = ";";
                csvLine
                    .append(traffic.getSourceUuid()).append(csvDelimiter)
                    .append(traffic.getSourceIp()).append(csvDelimiter)
                    .append(traffic.getDestinationUuid()).append(csvDelimiter)
                    .append(traffic.getDestinationIp()).append(csvDelimiter)
                    .append(traffic.getDestinationPort()).append(csvDelimiter)
                    .append(traffic.getProtocol()).append(csvDelimiter)
                    .append(traffic.getApplication()).append(csvDelimiter)
                    .append(traffic.getRuleName()).append(csvDelimiter)
                    .append(traffic.getNumber()).append(csvDelimiter)
                    .append(traffic.getLastCommunicationDatetime());
                writer.write(csvLine.toString());
                writer.newLine();
            } catch (IOException e) {
                LOGGER.error("Failed to write syslog message to text file", e);
            }
        }
    }

    private boolean isIgnoredHost(PaloAltoSyslogMessage traffic) {
        for (String host : syslogServerProperties.getFocusedHost()) {
            if (traffic.getSrc().equalsIgnoreCase(host) || traffic.getDst().equalsIgnoreCase(host)) {
                return false;
            }
        }
        return !syslogServerProperties.getFocusedHost().isEmpty();
    }

    public Map<String, Map<String, Traffic>> getTraffics() {
        return trafficRepository.getTraffics();
    }

}
