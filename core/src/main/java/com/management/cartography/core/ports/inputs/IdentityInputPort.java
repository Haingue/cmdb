package com.management.cartography.core.ports.inputs;

import com.management.cartography.core.models.business.identity.UserGroup;

public interface IdentityInputPort {

    UserGroup createUserGroup(UserGroup userGroup);

}
