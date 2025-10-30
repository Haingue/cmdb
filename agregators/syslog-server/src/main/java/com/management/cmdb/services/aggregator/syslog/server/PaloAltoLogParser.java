package com.management.cmdb.services.aggregator.syslog.server;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaloAltoLogParser {

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "(?<receive_time>\\S+ \\d{2} \\d{2}:\\d{2}:\\d{2})" +
                    ".*serial=(?<serial>\\S+)" +
                    ".*type=(?<type>\\S+)" +
                    ".*subtype=(?<subtype>\\S+)" +
                    ".*time_generated=(?<time_generated>\\S+ \\d{2} \\d{2}:\\d{2}:\\d{2})" +
                    ".*src=(?<src>\\S+)" +
                    ".*dst=(?<dst>\\S+)" +
                    ".*natsrc=(?<natsrc>\\S*)" +
                    ".*natdst=(?<natdst>\\S*)" +
                    ".*rule=(?<rule>\\S+)" +
                    ".*app=(?<app>\\S+)" +
                    ".*action=(?<action>\\S+)" +
                    ".*bytes=(?<bytes>\\d+)" +
                    ".*session_end_reason=(?<session_end_reason>\\S+)" +
                    ".*flags=(?<flags>\\d+)" +
                    ".*proto=(?<proto>\\S+)" +
                    ".*sport=(?<sport>\\d+)" +
                    ".*dport=(?<dport>\\d+)" +
                    ".*sessionid=(?<sessionid>\\d+)" +
                    ".*repeatcnt=(?<repeatcnt>\\d+)" +
                    ".*bytes_sent=(?<bytes_sent>\\d+)" +
                    ".*bytes_received=(?<bytes_received>\\d+)" +
                    ".*packets=(?<packets>\\d+)" +
                    ".*start=(?<start>\\S+ \\d{2} \\d{2}:\\d{2}:\\d{2})" +
                    ".*elapsed=(?<elapsed>\\d+)" +
                    ".*category=(?<category>\\S+)" +
                    ".*seqno=(?<seqno>\\d+)" +
                    ".*srcloc=(?<srcloc>\\S+)" +
                    ".*dstloc=(?<dstloc>\\S+)" +
                    ".*pkts_sent=(?<pkts_sent>\\d+)" +
                    ".*pkts_received=(?<pkts_received>\\d+)" +
                    ".*device_name=(?<device_name>\\S+)" +
                    ".*action_source=(?<action_source>\\S+)" +
                    ".*rule_uuid=(?<rule_uuid>\\S+)"
    );

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static PaloAltoTrafficLog parse(String rawLog) {
        Matcher matcher = LOG_PATTERN.matcher(rawLog);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid log format");
        }

        // PaloAltoTrafficLog log = new PaloAltoTrafficLog();
        /*
        log.setReceiveTime(parseTime(matcher.group("receive_time")));
        log.setSerial(matcher.group("serial"));
        log.setType(matcher.group("type"));
        log.setSubtype(matcher.group("subtype"));
        log.setTimeGenerated(parseTime(matcher.group("time_generated")));
        log.setSrc(matcher.group("src"));
        log.setDst(matcher.group("dst"));
        log.setNatsrc(matcher.group("natsrc"));
        log.setNatdst(matcher.group("natdst"));
        log.setRule(matcher.group("rule"));
        log.setApp(matcher.group("app"));
        log.setAction(matcher.group("action"));
        log.setBytes(Long.parseLong(matcher.group("bytes")));
        log.setFlags(Integer.parseInt(matcher.group("flags")));
        log.setProto(matcher.group("proto"));
        log.setSport(Integer.parseInt(matcher.group("sport")));
        log.setDport(Integer.parseInt(matcher.group("dport")));
        log.setSessionId(matcher.group("sessionid"));
        log.setRepeatCnt(Integer.parseInt(matcher.group("repeatcnt")));
        log.setBytesSent(Long.parseLong(matcher.group("bytes_sent")));
        log.setBytesReceived(Long.parseLong(matcher.group("bytes_received")));
        log.setPackets(Long.parseLong(matcher.group("packets")));
        log.setStart(parseTime(matcher.group("start")));
        log.setElapsed(Long.parseLong(matcher.group("elapsed")));
        log.setCategory(matcher.group("category"));
        log.setSeqno(Long.parseLong(matcher.group("seqno")));
        log.setSrcloc(matcher.group("srcloc"));
        log.setDstloc(matcher.group("dstloc"));
        log.setPktsSent(Long.parseLong(matcher.group("pkts_sent")));
        log.setPktsReceived(Long.parseLong(matcher.group("pkts_received")));
        log.setSessionEndReason(matcher.group("session_end_reason"));
        log.setDeviceName(matcher.group("device_name"));
        log.setActionSource(matcher.group("action_source"));
        log.setRuleUuid(matcher.group("rule_uuid"));
         */

        return null;
    }

    private static Instant parseTime(String timeStr) {
        return Instant.from(TIME_FORMATTER.parse(timeStr));
    }
}
