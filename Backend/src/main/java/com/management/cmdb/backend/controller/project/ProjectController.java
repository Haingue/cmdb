package com.management.cmdb.backend.controller.project;

import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.business.request.ProjectCreationRequest;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service/project")
public class ProjectController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProjectController.class);

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectCreationRequest request) {
        LOGGER.info("New project creation request: {}", request);
        return ResponseEntity.ok(request.getProject());
    }

}
