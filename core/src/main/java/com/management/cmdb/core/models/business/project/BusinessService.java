package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import org.apache.commons.lang3.StringUtils;

public record BusinessService (
        String name,
        String abbreviation
) {

    public boolean isValid () {
        if (StringUtils.isBlank(name)) throw new InvalidObjectException("name cannot be blank", this);
        if (StringUtils.isBlank(abbreviation)) throw new InvalidObjectException("abbreviation cannot be blank", this);
        if (abbreviation.length() != 3) throw new InvalidObjectException("abbreviation length must be equals to 3", this);
        return true;
    }

}
