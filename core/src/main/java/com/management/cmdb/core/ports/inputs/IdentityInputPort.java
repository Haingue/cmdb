package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.business.technology.Technology;

import java.util.List;
import java.util.UUID;

public interface IdentityInputPort {

    UserGroup createUserGroup(UserGroup userGroup);
    UserGroup updateUserGroup(UserGroup userGroup);

    List<UserGroup> findAllUserGroupByMember(UUID memberUuid);
    boolean hasAuthority (UUID memberUuid, Project project);
    boolean hasAuthority (UUID memberUuid, Environment environment);
    boolean hasAuthority (UUID memberUuid, Component component);
    boolean hasAuthority (UUID memberUuid, Technology technology);

}
