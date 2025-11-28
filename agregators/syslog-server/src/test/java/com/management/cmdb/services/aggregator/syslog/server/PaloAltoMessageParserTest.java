package com.management.cmdb.services.aggregator.syslog.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaloAltoMessageParserTest {

    @Test
    void shouldParseValidMessage() {
        String originalMessage = "<190>Nov 27 17:37:08 frvalfw11 1,2025/11/27 17:37:07,013201030824,TRAFFIC,end,2817,2025/11/27 17:37:07,10.114.181.101,10.114.214.8,0.0.0.0,0.0.0.0,VLAN801_TO_ANY,,,incomplete,vsys1,Zone_OT_DMZ_801,Zone_OT_DMZ,ae1.801,ae1.51,default,2025/11/27 17:37:07,2780227,1,34252,4662,0,0,0x1b,tcp,allow,142,78,64,2,2025/11/27 17:37:01,0,any,,7536179828408605381,0x8000000000000000,10.0.0.0-10.255.255.255,10.0.0.0-10.255.255.255,,1,1,tcp-rst-from-server,15,16,0,0,vsys1,frvalfw11,from-policy,,,0,,0,,N/A,0,0,0,0,37a6ae07-09a2-4c1d-8641-a18c1c610803,0,0,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,2025-11-27T17:37:08.128+01:00,,,unknown,unknown,unknown,1,,,incomplete,no,no,0,NonProxyTraffic,";
        PaloAltoSyslogMessage parsedMessage = PaloAltoMessageParser.parse(originalMessage);
        assertEquals(190, parsedMessage.getPriority());
        assertEquals("2025-11-27T16:37:08Z", parsedMessage.getTimestamp().toString());
        assertEquals("frvalfw11", parsedMessage.getHostname());
        assertEquals("10.114.181.101", parsedMessage.getSrc());
        assertEquals("10.114.214.8", parsedMessage.getDst());
        assertEquals("TRAFFIC", parsedMessage.getType());
        assertEquals("end", parsedMessage.getSubtype());
        assertEquals("013201030824", parsedMessage.getSerial());
        assertEquals("2025-11-27T17:37:07", parsedMessage.getTimeGenerated().toString());
        assertEquals("10.114.181.101", parsedMessage.getSrc());
        assertEquals("10.114.214.8", parsedMessage.getDst());
        assertEquals("0.0.0.0", parsedMessage.getNatsrc());
        assertEquals("VLAN801_TO_ANY", parsedMessage.getRule());
        assertEquals("incomplete", parsedMessage.getApp());
        assertEquals("vsys1", parsedMessage.getVsysName());
        assertEquals("NonProxyTraffic", parsedMessage.getFlowType());
    }

}