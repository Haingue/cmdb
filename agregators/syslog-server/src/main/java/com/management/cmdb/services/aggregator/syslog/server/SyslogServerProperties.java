package com.management.cmdb.services.aggregator.syslog.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("syslog.server")
public class SyslogServerProperties {

    private boolean udpEnable = true;
    private int udpPort = 514;
    private boolean tcpEnable = false;
    private int tcpPort = 601;

    public boolean isUdpEnable() {
        return udpEnable;
    }

    public void setUdpEnable(boolean udpEnable) {
        this.udpEnable = udpEnable;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public boolean isTcpEnable() {
        return tcpEnable;
    }

    public void setTcpEnable(boolean tcpEnable) {
        this.tcpEnable = tcpEnable;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }
}
