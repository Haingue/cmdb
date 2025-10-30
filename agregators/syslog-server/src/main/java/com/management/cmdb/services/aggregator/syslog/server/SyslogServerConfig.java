package com.management.cmdb.services.aggregator.syslog.server;

import com.management.cmdb.services.aggregator.syslog.service.SyslogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.udp.UdpServer;

@Configuration
public class SyslogServerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogServerConfig.class);

    //@Bean
    public Mono<? extends Connection> udpSyslogServer(SyslogServerProperties properties, SyslogService syslogService) {
        LOGGER.info("Initializing Syslog UDP server on port {}", properties.getPort());
        return UdpServer.create()
                .host("0.0.0.0")
                .port(properties.getPort())
                .handle((in, out) -> {
                    String logMessage = in.toString();
                    LOGGER.info("Received Syslog Message: {}", logMessage);
                    syslogService.syslogAnalysis(logMessage);
                    return out.sendString(Mono.just("OK"));
                })
                .bind()
                .doOnNext(connection ->
                        LOGGER.info("Syslog UDP server started on port 514")
                )
                .doOnError(error ->
                        LOGGER.error("Failed to start Syslog UDP server", error)
                )
                .onErrorResume(error -> {
                    LOGGER.error("Failed to start Syslog UDP server", error);
                    return Mono.empty();
                });
    }

}
