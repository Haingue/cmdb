package com.management.cmdb.inventory.service.controller;

import jakarta.annotation.Resource;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.RedirectView;

@Controller
@EnableJpaAuditing
public class MainController {

    @Resource
    private SpringDocConfigProperties springDocConfigProperties;

    @GetMapping
    public RedirectView loadMainPage() {
        return new RedirectView(springDocConfigProperties.getApiDocs().getPath());
    }
}
