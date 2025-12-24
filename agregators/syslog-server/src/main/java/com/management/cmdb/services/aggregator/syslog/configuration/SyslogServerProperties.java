package com.management.cmdb.services.aggregator.syslog.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("syslog.server")
public class SyslogServerProperties {

    private boolean udpEnable = true;
    private int udpPort = 514;
    private boolean tcpEnable = false;
    private int tcpPort = 601;
    private List<String> focusedHost = new ArrayList<>();

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

    public List<String> getFocusedHost() {
        return focusedHost;
    }

    public void setFocusedHost(List<String> focusedHost) {
        this.focusedHost = focusedHost;
    }
}
