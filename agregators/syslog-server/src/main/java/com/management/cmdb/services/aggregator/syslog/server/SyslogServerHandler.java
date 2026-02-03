package com.management.cmdb.services.aggregator.syslog.server;

import com.management.cmdb.services.aggregator.syslog.service.SyslogService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class SyslogServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogServerHandler.class);

    private final SyslogService syslogService;

    public SyslogServerHandler(SyslogService syslogService) {
        this.syslogService = syslogService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        String logMessage;
        switch (message) {
            case DatagramPacket datagramPacket -> logMessage = datagramPacket.content().toString(java.nio.charset.StandardCharsets.UTF_8);
            case String messageStr -> logMessage = messageStr;
            default -> logMessage = message.toString();
        }
        syslogService.syslogAnalysis(logMessage);
    }
}
