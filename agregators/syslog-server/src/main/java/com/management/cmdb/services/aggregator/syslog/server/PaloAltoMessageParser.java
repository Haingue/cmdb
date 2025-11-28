package com.management.cmdb.services.aggregator.syslog.server;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaloAltoMessageParser {

    private static final Pattern SYSLOG_PATTERN = Pattern.compile("<(\\d+)>(... \\d+ \\d+:\\d+:\\d+)\\s+(\\S+)\\s+(\\d+),(.+)");


    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd HH:mm:ss");

    public static PaloAltoSyslogMessage parse(String syslogMessage) {
        Matcher matcher = SYSLOG_PATTERN.matcher(syslogMessage);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid log format");
        }

        PaloAltoSyslogMessage message = new PaloAltoSyslogMessage();
        if (matcher.matches()) {
            message.setPriority(Integer.parseInt(matcher.group(1)));
            String timestampStr = matcher.group(2);
            message.setTimestamp(parseTimestamp(timestampStr));
            message.setHostname(matcher.group(3));

            // BSD format
            String payload = matcher.group(5);
            parseBsdPayload(message, payload);
        }
        return message;
    }

    private static Instant parseTimestamp(String timestampStr) {
        // Ajoute l'année courante pour compléter la date
        int year = java.time.Year.now().getValue();
        String fullTimestampStr = String.format("%d %s", year, timestampStr);
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm:ss", Locale.ENGLISH);
        return LocalDateTime.parse(fullTimestampStr, formatter)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    private static void parseBsdPayload(PaloAltoSyslogMessage message, String payload) {
        String[] fields = payload.split(",", -1);
        if (fields.length > 0) message.setReceiveTime(fields[0]);
        if (fields.length > 1) message.setSerial(fields[1]);
        if (fields.length > 2) message.setType(fields[2]);
        if (fields.length > 3) message.setSubtype(fields[3]);
        // Skip unknown value (2817)
        if (fields.length > 5) message.setTimeGenerated(fields[5]);
        if (fields.length > 6) message.setSrc(fields[6]);
        if (fields.length > 7) message.setDst(fields[7]);
        if (fields.length > 8) message.setNatsrc(fields[8]);
        if (fields.length > 9) message.setNatdst(fields[9]);
        if (fields.length > 10) message.setRule(fields[10]);
        if (fields.length > 11) message.setSrcuser(fields[11]);
        if (fields.length > 12) message.setDstuser(fields[12]);
        if (fields.length > 13) message.setApp(fields[13]);
        if (fields.length > 14) message.setVsys(fields[14]);
        if (fields.length > 15) message.setFrom(fields[15]);
        if (fields.length > 16) message.setTo(fields[16]);
        if (fields.length > 17) message.setInboundIf(fields[17]);
        if (fields.length > 18) message.setOutboundIf(fields[18]);
        if (fields.length > 19) message.setLogset(fields[19]);
        // Skip unknown value (timestamp)
        if (fields.length > 21) message.setSessionid(fields[21]);
        if (fields.length > 22) message.setRepeatcnt(fields[22]);
        if (fields.length > 23) message.setSport(fields[23]);
        if (fields.length > 24) message.setDport(fields[24]);
        if (fields.length > 25) message.setNatsport(fields[25]);
        if (fields.length > 26) message.setNatdport(fields[26]);
        if (fields.length > 27) message.setFlags(fields[27]);
        if (fields.length > 28) message.setProto(fields[28]);
        if (fields.length > 29) message.setAction(fields[29]);
        if (fields.length > 30) message.setBytes(fields[30]);
        if (fields.length > 31) message.setBytesSent(fields[31]);
        if (fields.length > 32) message.setBytesReceived(fields[32]);
        if (fields.length > 33) message.setPackets(fields[33]);
        if (fields.length > 34) message.setStart(fields[34]);
        if (fields.length > 35) message.setElapsed(fields[35]);
        if (fields.length > 36) message.setCategory(fields[36]);
        // Skip the empty field
        if (fields.length > 38) message.setSeqno(fields[38]);
        if (fields.length > 39) message.setActionflags(fields[39]);
        if (fields.length > 40) message.setSrcloc(fields[40]);
        if (fields.length > 41) message.setDstloc(fields[41]);
        // Skip the empty box
        if (fields.length > 43) message.setPktsSent(fields[43]);
        if (fields.length > 44) message.setPktsReceived(fields[44]);
        if (fields.length > 45) message.setSessionEndReason(fields[45]);
        // Skip dg_hier_level_1 to dg_hier_level_4
        if (fields.length > 50) message.setVsysName(fields[50]);
        if (fields.length > 51) message.setDeviceName(fields[51]);
        if (fields.length > 52) message.setActionSource(fields[52]);
        if (fields.length > 53) message.setSrcUuid(fields[53]);
        if (fields.length > 54) message.setDstUuid(fields[54]);
        if (fields.length > 55) message.setTunnelidImsi(fields[55]);
        if (fields.length > 56) message.setMonitortagImei(fields[56]);
        if (fields.length > 57) message.setParentSessionId(fields[57]);
        if (fields.length > 58) message.setParentStartTime(fields[58]);
        if (fields.length > 59) message.setTunnel(fields[59]);
        if (fields.length > 60) message.setAssocId(fields[60]);
        if (fields.length > 61) message.setChunks(fields[61]);
        if (fields.length > 62) message.setChunksSent(fields[62]);
        if (fields.length > 63) message.setChunksReceived(fields[63]);
        if (fields.length > 64) message.setRuleUuid(fields[64]);
        if (fields.length > 65) message.setHttp2Connection(fields[65]);
        if (fields.length > 66) message.setLinkChangeCount(fields[66]);
        if (fields.length > 67) message.setPolicyId(fields[67]);
        if (fields.length > 68) message.setLinkSwitches(fields[68]);
        if (fields.length > 69) message.setSdwanCluster(fields[69]);
        if (fields.length > 70) message.setSdwanDeviceType(fields[70]);
        if (fields.length > 71) message.setSdwanClusterType(fields[71]);
        if (fields.length > 72) message.setSdwanSite(fields[72]);
        if (fields.length > 73) message.setDynusergroupName(fields[73]);
        if (fields.length > 74) message.setXffIp(fields[74]);
        if (fields.length > 75) message.setSrcCategory(fields[75]);
        if (fields.length > 76) message.setSrcProfile(fields[76]);
        if (fields.length > 77) message.setSrcModel(fields[77]);
        if (fields.length > 78) message.setSrcVendor(fields[78]);
        if (fields.length > 79) message.setSrcOsfamily(fields[79]);
        if (fields.length > 80) message.setSrcOsversion(fields[80]);
        if (fields.length > 81) message.setSrcHost(fields[81]);
        if (fields.length > 82) message.setSrcMac(fields[82]);
        if (fields.length > 83) message.setDstCategory(fields[83]);
        if (fields.length > 84) message.setDstProfile(fields[84]);
        if (fields.length > 85) message.setDstModel(fields[85]);
        if (fields.length > 86) message.setDstVendor(fields[86]);
        if (fields.length > 87) message.setDstOsfamily(fields[87]);
        if (fields.length > 88) message.setDstOsversion(fields[88]);
        if (fields.length > 89) message.setDstHost(fields[89]);
        if (fields.length > 90) message.setDstMac(fields[90]);
        if (fields.length > 91) message.setContainerId(fields[91]);
        if (fields.length > 92) message.setPodNamespace(fields[92]);
        if (fields.length > 93) message.setPodName(fields[93]);
        if (fields.length > 94) message.setSrcEdl(fields[94]);
        if (fields.length > 95) message.setDstEdl(fields[95]);
        if (fields.length > 96) message.setHostid(fields[96]);
        if (fields.length > 97) message.setSerialnumber(fields[97]);
        if (fields.length > 98) message.setSrcDag(fields[98]);
        if (fields.length > 99) message.setDstDag(fields[99]);
        if (fields.length > 100) message.setSessionOwner(fields[100]);
        if (fields.length > 101) message.setHighResTimestamp(fields[101]);
        if (fields.length > 102) message.setNssaiSst(fields[102]);
        if (fields.length > 103) message.setNssaiSd(fields[103]);
        if (fields.length > 104) message.setSubcategoryOfApp(fields[104]);
        if (fields.length > 105) message.setCategoryOfApp(fields[105]);
        if (fields.length > 106) message.setTechnologyOfApp(fields[106]);
        if (fields.length > 107) message.setRiskOfApp(fields[107]);
        if (fields.length > 108) message.setCharacteristicOfApp(fields[108]);
        if (fields.length > 109) message.setContainerOfApp(fields[109]);
        if (fields.length > 110) message.setTunneledApp(fields[110]);
        if (fields.length > 111) message.setIsSaasOfApp(fields[111]);
        if (fields.length > 112) message.setSanctionedStateOfApp(fields[112]);

        if (fields.length > 113) message.setOffloaded(fields[113]);
        if (fields.length > 114) message.setFlowType(fields[114]);
        if (fields.length > 115) message.setClusterName(fields[115]);
    }
}