package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.services.inventory.model.Environment;
import com.management.cmdb.services.inventory.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectRepositoryTest {

    @Autowired
    ProjectRepository projectRepository;

    @Test
    void shouldSaveProject() {
        Project project = Project.builder()
                .uuid(UUID.randomUUID())
                .fullName("Test Project")
                .shortName("TP")
                .description("Test Project description")
                .environments(Set.of(
                        Environment.builder().type(EnvironmentType.TEST).status(EnvironmentStatus.DEPLOYED).location("FR").build(),
                        Environment.builder().type(EnvironmentType.ACC).status(EnvironmentStatus.READY).location("FR").build(),
                        Environment.builder().type(EnvironmentType.PROD).status(EnvironmentStatus.REQUESTED).location("FR").build()
                ))
                .build();
        Project result = this.projectRepository.save(project).block();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(project.getUuid(), result.getUuid());
        Assertions.assertEquals(project.getFullName(), result.getFullName());
        Assertions.assertEquals(project.getShortName(), result.getShortName());
        Assertions.assertEquals(project.getDescription(), result.getDescription());
        Assertions.assertEquals(project.getEnvironments().stream().findFirst(), result.getEnvironments().stream().findFirst());
    }

}