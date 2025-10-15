package com.management.cmdb.core.models.business.identity;

import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public record UserGroup(
        String name,
        String email,
        String description,
        UserGroup owner,
        Set<User> members
) {

    public boolean isValid() throws InvalidObjectException {
        if (StringUtils.isBlank(this.name())) throw new InvalidObjectException("name is blank", this);
        if (this.owner() == null) throw new InvalidObjectException("owner is blank", this);
        if (this.members() == null || this.members.isEmpty()) throw new InvalidObjectException("there is no member", this);
        return true;
    }

}
