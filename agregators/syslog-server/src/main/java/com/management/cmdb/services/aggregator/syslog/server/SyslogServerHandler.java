package com.management.cmdb.services.aggregator.syslog.server;

import com.management.cmdb.services.aggregator.syslog.service.SyslogService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SyslogServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogServerHandler.class);

    private final SyslogService syslogService;

    public SyslogServerHandler(SyslogService syslogService) {
        this.syslogService = syslogService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        LOGGER.debug("Received Syslog Message: {}", datagramPacket);
        String logMessage = datagramPacket.content().toString(java.nio.charset.StandardCharsets.UTF_8);
        // TODO: Ajoutez la logique pour traiter le log (ex : sauvegarde en base de données)
        syslogService.syslogAnalysis(logMessage);
    }
}
