package com.management.cmdb.services.inventory.controller;

import com.management.cmdb.services.inventory.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private final ProjectRepository projectRepository;

    public TestController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public ResponseEntity test() {
        return ResponseEntity.ok(projectRepository.findAll());
    }

}
