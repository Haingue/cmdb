package com.management.cmdb.core.models.exceptions;

import com.management.cmdb.core.models.business.project.BusinessService;

public class InvalidObjectException extends CoreException {
    public InvalidObjectException(String message) {
        super(message);
    }
    public InvalidObjectException(String message, Object object) {
        super(String.format("%s is not valid: %s [%s]", object.getClass().getSimpleName(), message, object));
    }
}
