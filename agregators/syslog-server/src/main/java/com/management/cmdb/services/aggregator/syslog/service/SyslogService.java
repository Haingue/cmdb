package com.management.cmdb.services.aggregator.syslog.service;

import com.management.cmdb.services.aggregator.syslog.server.PaloAltoMessageParser;
import com.management.cmdb.services.aggregator.syslog.server.PaloAltoSyslogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SyslogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogService.class);

    public void syslogAnalysis(String message) {
        PaloAltoSyslogMessage parsedMessage = PaloAltoMessageParser.parse(message);
        if (parsedMessage.getType().equals("TRAFFIC")) {
            LOGGER.debug("Traffic log: {}", parsedMessage);
        }
    }

}
