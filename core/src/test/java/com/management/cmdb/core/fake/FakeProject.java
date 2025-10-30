package com.management.cmdb.core.fake;

import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.Project;

import java.util.Set;

public enum FakeProject {

    PROJECT_1(new Project("Project 1", "P1", "Fake project", FakeBusinessService.BUSINESS_SERVICE_1.businessService, FakeUserGroup.TECHNICAL_GROUP.userGroup, FakeUserGroup.BUSINESS_GROUP.userGroup));

    public final Project project;

    FakeProject(Project project) {
        this.project = project;
    }

}
