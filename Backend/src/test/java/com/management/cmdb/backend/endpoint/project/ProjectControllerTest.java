package com.management.cmdb.backend.endpoint.project;

import com.management.cmdb.backend.endpoint.project.dto.ProjectDto;
import com.management.cmdb.backend.endpoint.project.mapper.ProjectMapper;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Project;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerTest {

    private final WebTestClient webTestClient;

    ProjectControllerTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    void createProjectUsingCore () {
        Project project = new Project("Project test", "Ptest",
                "This is a test", new BusinessService("TEST", "TES"),
                new UserGroup("ADMIN", "", "", null, Set.of()),
                new UserGroup("ADMIN", "", "", null, Set.of()),
                Set.of());
        webTestClient.post()
                .uri("/project")
                .body(Mono.just(ProjectMapper.INSTANCE.toDto(project)), ProjectDto.class)
                .exchange()
                .expectStatus().isCreated();
    }

}