package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.fake.FakeBusinessService;
import com.management.cmdb.core.fake.FakeProject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LinkTest {

    @Test
    void testLinkCreation() {
        BusinessService businessService = FakeBusinessService.BUSINESS_SERVICE_1.businessService;
        Project project = FakeProject.PROJECT_1.project;

        Link link = new Link();
        link.setUuid(UUID.randomUUID());
        link.setProject(project);
        link.setBusinessService(businessService);

        assertNotNull(link.getUuid());
        assertEquals(project, link.getProject());
        assertEquals(businessService, link.getBusinessService());
    }

    @Test
    void testLinkEquality() {
        BusinessService businessService = FakeBusinessService.BUSINESS_SERVICE_1.businessService;
        Project project = FakeProject.PROJECT_1.project;

        Link link1 = Link.builder()
                .uuid(UUID.randomUUID())
                .project(project)
                .businessService(businessService)
                .build();

        Link link2 = Link.builder()
                .uuid(link1.getUuid())
                .project(project)
                .businessService(businessService)
                .build();

        assertEquals(link1, link2);
    }

    @Test
    void testLinkHashCode() {
        BusinessService businessService = FakeBusinessService.BUSINESS_SERVICE_1.businessService;
        Project project = FakeProject.PROJECT_1.project;

        Link link1 = Link.builder()
                .uuid(UUID.randomUUID())
                .project(project)
                .businessService(businessService)
                .build();

        Link link2 = Link.builder()
                .uuid(link1.getUuid())
                .project(project)
                .businessService(businessService)
                .build();

        assertEquals(link1.hashCode(), link2.hashCode());
    }

}