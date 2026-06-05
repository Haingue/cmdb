package com.management.cmdb.core.fake;

import com.management.cmdb.core.models.business.constant.TechnologyType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;

public enum FakeTechnology {

    JAVA_SPRING(
            Technology.builder()
                    .name("Java Spring Boot")
                    .description("Java backend framework")
                    .type(TechnologyType.BACKEND)
                    .programmingLanguage("Java")
                    .minimalVersion(new Version(3, 2, 0))
                    .targetVersion(new Version(3, 3, 0))
                    .lastVersion(new Version(3, 3, 5))
                    .build()
    ),

    POSTGRESQL(
            Technology.builder()
                    .name("PostgreSQL")
                    .description("Relational database")
                    .type(TechnologyType.DATABASE)
                    .programmingLanguage(null)
                    .minimalVersion(new Version(15, 0, 0))
                    .targetVersion(new Version(16, 0, 0))
                    .lastVersion(new Version(16, 2, 0))
                    .build()
    ),

    REACT(
            Technology.builder()
                    .name("React")
                    .description("Frontend framework")
                    .type(TechnologyType.FRONTEND)
                    .programmingLanguage("JavaScript")
                    .minimalVersion(new Version(18, 0, 0))
                    .targetVersion(new Version(18, 2, 0))
                    .lastVersion(new Version(18, 2, 0))
                    .build()
    ),

    REDIS(
            Technology.builder()
                    .name("Redis")
                    .description("In-memory cache")
                    .type(TechnologyType.DATABASE)
                    .programmingLanguage(null)
                    .minimalVersion(new Version(7, 0, 0))
                    .targetVersion(new Version(7, 2, 0))
                    .lastVersion(new Version(7, 4, 0))
                    .build()
    ),

    OUTDATED_TOMCAT(
            Technology.builder()
                    .name("Apache Tomcat")
                    .description("Servlet container")
                    .type(TechnologyType.OPERATING_SYSTEM)
                    .programmingLanguage("Java")
                    .minimalVersion(new Version(8, 5, 0))
                    .targetVersion(new Version(10, 1, 0))
                    .lastVersion(new Version(10, 1, 20))
                    .build()
    );

    public final Technology technology;

    FakeTechnology(Technology technology) {
        this.technology = technology;
    }
}
