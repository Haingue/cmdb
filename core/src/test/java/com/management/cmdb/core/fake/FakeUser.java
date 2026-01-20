package com.management.cmdb.core.fake;

import com.management.cmdb.core.models.business.identity.User;

import java.util.Set;
import java.util.UUID;

public enum FakeUser {

    SUPER_ADMIN(new User(UUID.randomUUID(), "SUPER ADMIN", "super.admin@fake.com", Set.of())),
    TECH_LEAD(new User(UUID.randomUUID(), "TECH LEAD", "tech.lead@fake.com", Set.of())),
    BUSINESS_MEMBER(new User(UUID.randomUUID(), "BUSINESS MEMBER", "business.member@fake.com", Set.of()));

    public final User user;

    FakeUser (User user) {
        this.user = user;
    }

}
