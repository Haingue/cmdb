package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.exceptions.*;
import com.management.cmdb.core.ports.inputs.EnvironmentInputPort;
import com.management.cmdb.core.ports.inputs.IdentityInputPort;
import com.management.cmdb.core.ports.outputs.EnvironmentOutputPort;
import com.management.cmdb.core.ports.outputs.IdentityOutputPort;
import com.management.cmdb.core.ports.outputs.ProjectOutputPort;

import java.util.List;
import java.util.UUID;

public class IdentityService implements IdentityInputPort {

    private final IdentityOutputPort identityOutputPort;
    private final ProjectOutputPort projectOutputPort;
    private final EnvironmentOutputPort environmentOutputPort;

    public IdentityService(IdentityOutputPort identityOutputPort, ProjectOutputPort projectOutputPort, EnvironmentOutputPort environmentOutputPort) {
        this.identityOutputPort = identityOutputPort;
        this.projectOutputPort = projectOutputPort;
        this.environmentOutputPort = environmentOutputPort;
    }

    @Override
    public UserGroup createUserGroup(UserGroup userGroup) {
        if (userGroup == null) throw new InvalidObjectException("UserGroup is null");
        if (!userGroup.isValid()) throw new InvalidObjectException("UserGroup is not valid", userGroup);

        return identityOutputPort.save(userGroup);
    }

    @Override
    public UserGroup updateUserGroup(UserGroup userGroup) throws InvalidObjectException {
        if (userGroup == null) throw new InvalidObjectException("UserGroup is null");
        if (!userGroup.isValid()) throw new InvalidObjectException("UserGroup is not valid", userGroup);

        UserGroup existingUserGroup = identityOutputPort.findOne(userGroup.name())
                .orElseThrow(() -> new NotFoundException(userGroup.name()));
        // TODO updates linked entities (ex: component, ...)
        return identityOutputPort.save(userGroup);
    }

    @Override
    public List<UserGroup> findAllUserGroupByMember(UUID memberUuid) {
        return this.identityOutputPort.findAllByMember(memberUuid);
    }

    @Override
    public boolean hasAuthority(UUID memberUuid, Project project) {
        if (memberUuid == null) throw new CoreException("Member uuid is null");
        if (project == null) throw new CoreException("Project is null");
        List<UserGroup> userGroupOfMember = this.findAllUserGroupByMember(memberUuid);

        return userGroupOfMember.stream().anyMatch(userGroup -> userGroup.equals(project.getOwners()) || userGroup.equals(project.getMaintainers()));
    }

    @Override
    public boolean hasAuthority(UUID memberUuid, Environment environment) {
        Project project = this.projectOutputPort.findByEnvironment(environment.getUuid())
                .orElseThrow(() -> new NotFoundException(environment.getUuid()));
        return this.hasAuthority(memberUuid, project);
    }

    @Override
    public boolean hasAuthority(UUID memberUuid, Component component) {
        Environment environment = environmentOutputPort.findByComponent(component.getUuid())
                .orElseThrow(() -> new NotFoundException(component.getUuid()));
        return this.hasAuthority(memberUuid, environment);
    }

    @Override
    public boolean hasAuthority(UUID memberUuid, Technology technology) {
        throw new NotImplemented();
    }
}
