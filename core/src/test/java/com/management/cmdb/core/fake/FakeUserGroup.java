package com.management.cmdb.core.fake;

import com.management.cmdb.core.models.business.identity.UserGroup;

import java.util.Set;

public enum FakeUserGroup {

    GLOBAL_ADMIN(new UserGroup("ADMIN_GROUP", "admin@fake.com", "Fake admin group", null, Set.of(FakeUser.SUPER_ADMIN.user))),
    TECHNICAL_GROUP(new UserGroup("TECHNICAL_GROUP", "technical@fake.com", "Fake technical group", FakeUserGroup.GLOBAL_ADMIN.userGroup, Set.of(FakeUser.TECH_LEAD.user))),
    BUSINESS_GROUP(new UserGroup("BUSINESS_GROUP", "business@fake.com", "Fake business group", FakeUserGroup.GLOBAL_ADMIN.userGroup, Set.of(FakeUser.BUSINESS_MEMBER.user)));

    public final UserGroup userGroup;

    FakeUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

}
