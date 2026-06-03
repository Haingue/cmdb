package com.management.cmdb.services.inventory.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@EnableJpaAuditing
public class MainController {

    @Resource
    private SwaggerUiConfigProperties swaggerUiConfigProperties;

    @GetMapping
    public void loadMainPage(HttpServletResponse response) throws IOException {
        response.sendRedirect(swaggerUiConfigProperties.getPath());
    }
}
