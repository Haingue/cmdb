package com.management.cmdb.services.aggregator.syslog.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Traffic {

    private UUID sourceUuid;
    private String sourceIp;
    private UUID destinationUuid;
    private String destinationIp;
    private int destinationPort;
    private String protocol;
    private String application;
    private String ruleName;
    private long number;
    private LocalDateTime lastCommunicationDatetime;

    @Override
    public Traffic clone() {
        return Traffic.builder()
                .sourceUuid(sourceUuid)
                .sourceIp(sourceIp)
                .destinationUuid(destinationUuid)
                .destinationIp(destinationIp)
                .destinationPort(destinationPort)
                .protocol(protocol)
                .application(application)
                .ruleName(ruleName)
                .number(number)
                .lastCommunicationDatetime(lastCommunicationDatetime)
                .build();
    }
}
