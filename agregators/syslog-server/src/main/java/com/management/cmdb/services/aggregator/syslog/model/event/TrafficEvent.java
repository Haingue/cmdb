package com.management.cmdb.services.aggregator.syslog.model.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class TrafficEvent {

    private String sourceIp;
    private String destinationIp;
    private int destinationPort;
    private String protocol;

}
