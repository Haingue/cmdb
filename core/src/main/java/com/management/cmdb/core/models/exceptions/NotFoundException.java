package com.management.cmdb.core.models.exceptions;

import com.management.cmdb.core.models.business.identity.UserGroup;

public class NotFoundException extends CoreException {
    public NotFoundException(Object identifier) {
        super(String.format("Object not found: %s", identifier));
    }
}
