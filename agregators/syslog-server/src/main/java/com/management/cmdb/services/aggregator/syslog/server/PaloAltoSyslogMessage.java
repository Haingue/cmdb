package com.management.cmdb.services.aggregator.syslog.server;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Source: https://docs.paloaltonetworks.com/ngfw/administration/monitoring/use-syslog-for-monitoring/syslog-field-descriptions/traffic-log-fields#idbe18d2d4-9eb8-4966-bec8-df3a6de70e66
 */
@Data
public class PaloAltoSyslogMessage {

    private int priority;
    private Instant timestamp;
    private String hostname;

    private String receiveTime;
    private String serial;
    private String type;
    private String subtype;
    private LocalDateTime timeGenerated;
    private String src;
    private String dst;
    private String natsrc;
    private String natdst;
    private String rule;
    private String srcuser;
    private String dstuser;
    private String app;
    private String vsys;
    private String from;
    private String to;
    private String inboundIf;
    private String outboundIf;
    private String logset;
    private String sessionid;
    private String repeatcnt;
    private String sport;
    private String dport;
    private String natsport;
    private String natdport;
    private String flags;
    private String proto;
    private String action;
    private String bytes;
    private String bytesSent;
    private String bytesReceived;
    private String packets;
    private String start;
    private String elapsed;
    private String category;
    private String seqno;
    private String actionflags;
    private String srcloc;
    private String dstloc;
    private String pktsSent;
    private String pktsReceived;
    private String sessionEndReason;
    private String vsysName;
    private String deviceName;
    private String actionSource;
    private String srcUuid;
    private String dstUuid;
    private String tunnelidImsi;
    private String monitortagImei;
    private String parentSessionId;
    private String parentStartTime;
    private String tunnel;
    private String assocId;
    private String chunks;
    private String chunksSent;
    private String chunksReceived;
    private String ruleUuid;
    private String http2Connection;
    private String linkChangeCount;
    private String policyId;
    private String linkSwitches;
    private String sdwanCluster;
    private String sdwanDeviceType;
    private String sdwanClusterType;
    private String sdwanSite;
    private String dynusergroupName;
    private String xffIp;
    private String srcCategory;
    private String srcProfile;
    private String srcModel;
    private String srcVendor;
    private String srcOsfamily;
    private String srcOsversion;
    private String srcHost;
    private String srcMac;
    private String dstCategory;
    private String dstProfile;
    private String dstModel;
    private String dstVendor;
    private String dstOsfamily;
    private String dstOsversion;
    private String dstHost;
    private String dstMac;
    private String containerId;
    private String podNamespace;
    private String podName;
    private String srcEdl;
    private String dstEdl;
    private String hostid;
    private String serialnumber;
    private String srcDag;
    private String dstDag;
    private String sessionOwner;
    private String highResTimestamp;
    private String nssaiSst;
    private String nssaiSd;
    private String subcategoryOfApp;
    private String categoryOfApp;
    private String technologyOfApp;
    private String riskOfApp;
    private String characteristicOfApp;
    private String containerOfApp;
    private String tunneledApp;
    private String isSaasOfApp;
    private String sanctionedStateOfApp;
    private String offloaded;
    private String flowType;
    private String clusterName;

    @Override
    public String toString() {
        return String.format(
                "SyslogMessage{receiveTime=%s, type=%s, src=%s, dst=%s, app=%s, action=%s, proto=%s}",
                receiveTime, type, src, dst, app, action, proto
        );
    }
}
