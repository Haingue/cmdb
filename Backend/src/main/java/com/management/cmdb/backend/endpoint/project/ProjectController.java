package com.management.cmdb.backend.endpoint.project;

import com.management.cmdb.backend.endpoint.project.dto.ProjectCreationRequest;
import com.management.cmdb.backend.endpoint.project.dto.ProjectDto;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/project")
public class ProjectController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProjectController.class);

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectCreationRequest request) {
        LOGGER.info("New project creation request: {}", request);
        return ResponseEntity.ok(request.getProject());
    }

}
