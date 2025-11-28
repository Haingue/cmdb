package com.management.cmdb.services.aggregator.syslog.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class SyslogTcpServer implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogTcpServer.class);

    private final SyslogServerProperties properties;
    private final SyslogServerHandler syslogServerHandler;
    private EventLoopGroup group;
    private Channel channel;
    private boolean isRunning = false;

    public SyslogTcpServer(SyslogServerProperties properties, SyslogServerHandler syslogServerHandler) {
        this.properties = properties;
        this.syslogServerHandler = syslogServerHandler;
    }

    @Override
    public void start() {
        if (!properties.isTcpEnable() || isRunning) {
            return;
        }
        group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(syslogServerHandler);
            channel = bootstrap.bind("0.0.0.0", properties.getTcpPort()).sync().channel();
            isRunning = true;
            LOGGER.info("Syslog server started on port [tcp={}]", properties.getTcpPort());
        } catch (InterruptedException e) {
            LOGGER.error("Failed to start Syslog server", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to start Syslog server", e);
        } catch (Exception e) {
            LOGGER.error("Failed to start Syslog server", e);
        }
    }

    @Override
    public void stop() {
        if (!isRunning) {
            return;
        }
        try {
            if (channel != null) {
                channel.close().sync();
            }
            if (group != null) {
                group.shutdownGracefully().sync();
            }
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
