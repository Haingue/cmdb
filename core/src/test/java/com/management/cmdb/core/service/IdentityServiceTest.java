package com.management.cmdb.core.service;

import com.management.cmdb.core.fake.FakeProject;
import com.management.cmdb.core.fake.FakeUser;
import com.management.cmdb.core.fake.FakeUserGroup;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.ports.outputs.EnvironmentOutputPort;
import com.management.cmdb.core.ports.outputs.IdentityOutputPort;
import com.management.cmdb.core.ports.outputs.ProjectOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

class IdentityServiceTest {

    private ProjectOutputPort projectOutputPort;
    private EnvironmentOutputPort environmentOutputPort;
    private IdentityOutputPort identityOutputPort;

    private IdentityService identityService;

    @BeforeEach
    public void setUp () {
        projectOutputPort = Mockito.mock(ProjectOutputPort.class);
        environmentOutputPort = Mockito.mock(EnvironmentOutputPort.class);
        identityOutputPort = Mockito.mock(IdentityOutputPort.class);

        identityService = new IdentityService(identityOutputPort, projectOutputPort, environmentOutputPort);
    }

    @Test
    public void shouldBeSavedNewUserGroup () {
        UserGroup originalUserGroup = FakeUserGroup.TECHNICAL_GROUP.userGroup;

        UserGroup newUserGroup = this.identityService.createUserGroup(originalUserGroup);
        Mockito.verify(identityOutputPort).save(originalUserGroup);
    }

    @Test
    public void shouldNotBeValid () {
        Assertions.assertThrows(InvalidObjectException.class, () -> this.identityService.createUserGroup(
                new UserGroup("", FakeUserGroup.TECHNICAL_GROUP.userGroup.email(), FakeUserGroup.TECHNICAL_GROUP.userGroup.description(), FakeUserGroup.TECHNICAL_GROUP.userGroup.owner(), FakeUserGroup.TECHNICAL_GROUP.userGroup.members())
        ));

        Assertions.assertThrows(InvalidObjectException.class, () -> this.identityService.createUserGroup(
                new UserGroup(FakeUserGroup.TECHNICAL_GROUP.userGroup.name(), FakeUserGroup.TECHNICAL_GROUP.userGroup.email(), FakeUserGroup.TECHNICAL_GROUP.userGroup.description(), null, FakeUserGroup.TECHNICAL_GROUP.userGroup.members())
        ));
    }

    @Test
    public void shouldUpdateUserGroup () {
        UserGroup originalUserGroup = FakeUserGroup.TECHNICAL_GROUP.userGroup;
        Mockito.when(identityOutputPort.findOne(originalUserGroup.name())).thenReturn(Optional.of(originalUserGroup));

        UserGroup newUserGroup = this.identityService.updateUserGroup(originalUserGroup);
        Mockito.verify(identityOutputPort).save(originalUserGroup);
    }

    @Test
    public void shouldNotUpdateUserGroup () {
        UserGroup originalUserGroup = FakeUserGroup.TECHNICAL_GROUP.userGroup;

        Assertions.assertThrows(NotFoundException.class, () -> this.identityService.updateUserGroup(originalUserGroup));
    }

    @Test
    public void shouldHaveAuthority () {
        Mockito.when(identityService.findAllUserGroupByMember(FakeUser.TECH_LEAD.user.uuid())).thenReturn(List.of(FakeProject.PROJECT_1.project.getMaintainers()));
        Mockito.when(identityService.findAllUserGroupByMember(FakeUser.BUSINESS_MEMBER.user.uuid())).thenReturn(List.of(FakeProject.PROJECT_1.project.getOwners()));

        Assertions.assertTrue(this.identityService.hasAuthority(FakeUser.TECH_LEAD.user.uuid(), FakeProject.PROJECT_1.project));
        Assertions.assertTrue(this.identityService.hasAuthority(FakeUser.BUSINESS_MEMBER.user.uuid(), FakeProject.PROJECT_1.project));
    }

    @Test
    public void shouldNotHaveAuthority () {
        Mockito.when(identityService.findAllUserGroupByMember(FakeUser.SUPER_ADMIN.user.uuid())).thenReturn(List.of());

        Assertions.assertFalse(this.identityService.hasAuthority(FakeUser.SUPER_ADMIN.user.uuid(), FakeProject.PROJECT_1.project));
    }
}