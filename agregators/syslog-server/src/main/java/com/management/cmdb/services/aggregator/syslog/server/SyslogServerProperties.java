package com.management.cmdb.services.aggregator.syslog.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("syslog.server")
public class SyslogServerProperties {

    private int port = 514;

    public int getPort() {
        return port;
    }
}
