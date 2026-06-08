package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.technical.Checkable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class BusinessService implements Serializable, Checkable {
    private String name;
    private String abbreviation;

    @Override
    public void checkIntegrity () throws InvalidObjectException {
        if (StringUtils.isBlank(name)) throw new InvalidObjectException("name cannot be blank", this);
        if (StringUtils.isBlank(abbreviation)) throw new InvalidObjectException("abbreviation cannot be blank", this);
        if (abbreviation.length() != 3)
            throw new InvalidObjectException("abbreviation length must be equals to 3", this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BusinessService that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(abbreviation, that.abbreviation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, abbreviation);
    }

    @Override
    public String toString() {
        return "BusinessService[" +
                "name=" + name + ", " +
                "abbreviation=" + abbreviation + ']';
    }


}
