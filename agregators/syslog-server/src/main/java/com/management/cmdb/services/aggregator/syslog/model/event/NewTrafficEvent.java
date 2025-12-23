package com.management.cmdb.services.aggregator.syslog.model.event;

import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class NewTrafficEvent extends TrafficEvent {

}
