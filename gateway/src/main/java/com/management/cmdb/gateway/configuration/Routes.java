package com.management.cmdb.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.ZonedDateTime;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.after;
import static org.springframework.web.servlet.function.RequestPredicates.GET;

@Component
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> routeConfig() {
        return route("inventory-service-custom")
                .GET("/inventory", http())
                .build();
    }

}
