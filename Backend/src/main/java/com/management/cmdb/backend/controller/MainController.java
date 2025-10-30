package com.management.cmdb.backend.controller;

import jakarta.annotation.Resource;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
public class MainController {
    @Resource
    private SwaggerUiConfigProperties swaggerUiConfigProperties;

    @GetMapping
    public Mono<Void> loadMainPage(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        serverWebExchange.getResponse().getHeaders().add("Location", swaggerUiConfigProperties.getPath());
        return serverWebExchange.getResponse().setComplete();
    }

    @GetMapping("/greeting")
    public String greeting () {
        return "Hello World";
    }

}
