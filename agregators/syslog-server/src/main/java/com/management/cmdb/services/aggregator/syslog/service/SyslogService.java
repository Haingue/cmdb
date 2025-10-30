package com.management.cmdb.services.aggregator.syslog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SyslogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogService.class);

    public void syslogAnalysis(String log) {
        LOGGER.info("Syslog analysis: {}", log);
    }

}
