package com.management.cmdb.services.aggregator.syslog.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class SyslogServer implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogServer.class);

    private final SyslogServerProperties properties;
    private final SyslogServerHandler syslogServerHandler;
    private EventLoopGroup group;
    private Channel channel;
    private boolean isRunning = false;

    public SyslogServer(SyslogServerProperties properties, SyslogServerHandler syslogServerHandler) {
        this.properties = properties;
        this.syslogServerHandler = syslogServerHandler;
    }

    @Override
    public void start() {
        if (isRunning) {
            return;
        }
        group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(syslogServerHandler);

            channel = bootstrap.bind("0.0.0.0", properties.getPort()).sync().channel();
            isRunning = true;
            LOGGER.info("Syslog server started on port {}", properties.getPort());
        } catch (InterruptedException e) {
            LOGGER.error("Failed to start Syslog server", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to start Syslog server", e);
        }
    }

    @Override
    public void stop() {
        if (!isRunning) {
            return;
        }
        try {
            channel.close().sync();
            group.shutdownGracefully().sync();
            isRunning = false;
            LOGGER.info("Syslog server stopped");
        } catch (InterruptedException e) {
            LOGGER.error("Failed to stop Syslog server", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to stop Syslog server", e);
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
