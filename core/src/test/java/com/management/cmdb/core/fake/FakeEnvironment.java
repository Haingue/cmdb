package com.management.cmdb.core.fake;

import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.business.project.Environment;

import java.util.UUID;

public enum FakeEnvironment {

    DEV_ENVIRONMENT(
            Environment.builder()
                    .uuid(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                    .name("PROJ-1-DEV-Paris")
                    .description("Development environment for Project 1 in Paris")
                    .location("Paris")
                    .type(EnvironmentType.DEV)
                    .status(EnvironmentStatus.READY)
                    .jiraTracker("PROJ1-DEV")
                    .build()
    ),

    TEST_ENVIRONMENT(
            Environment.builder()
                    .uuid(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                    .name("PROJ-1-TEST-Paris")
                    .description("Test environment for Project 1 in Paris")
                    .location("Paris")
                    .type(EnvironmentType.TEST)
                    .status(EnvironmentStatus.READY)
                    .jiraTracker("PROJ1-TEST")
                    .build()
    ),

    PREPROD_ENVIRONMENT(
            Environment.builder()
                    .uuid(UUID.fromString("00000000-0000-0000-0000-000000000003"))
                    .name("PROJ-1-PREPROD-Paris")
                    .description("Pre-production environment for Project 1")
                    .location("Paris")
                    .type(EnvironmentType.PRE_PROD)
                    .status(EnvironmentStatus.DEPLOYED)
                    .jiraTracker("PROJ1-PREPROD")
                    .build()
    ),

    PROD_ENVIRONMENT(
            Environment.builder()
                    .uuid(UUID.fromString("00000000-0000-0000-0000-000000000004"))
                    .name("PROJ-1-PROD-Paris")
                    .description("Production environment for Project 1")
                    .location("Paris")
                    .type(EnvironmentType.PROD)
                    .status(EnvironmentStatus.DEPLOYED)
                    .jiraTracker("PROJ1-PROD")
                    .build()
    ),

    ACC_ENVIRONMENT(
            Environment.builder()
                    .uuid(UUID.fromString("00000000-0000-0000-0000-000000000005"))
                    .name("PROJ-1-ACC-Lyon")
                    .description("Acceptance environment for Project 1 in Lyon")
                    .location("Lyon")
                    .type(EnvironmentType.ACC)
                    .status(EnvironmentStatus.IN_PROGRESS)
                    .jiraTracker("PROJ1-ACC")
                    .build()
    );

    public final Environment environment;

    FakeEnvironment(Environment environment) {
        this.environment = environment;
    }
}
