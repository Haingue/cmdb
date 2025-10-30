package com.management.cmdb.core.ports.outputs;

import com.management.cmdb.core.models.business.identity.UserGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IdentityOutputPort {

    Optional<UserGroup> findOne (String name);
    List<UserGroup> findAllByMember (UUID memberId);
    UserGroup save (UserGroup userGroup);

}
