package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.identity.UserGroup;

public interface IdentityInputPort {

    UserGroup createUserGroup(UserGroup userGroup);

}
