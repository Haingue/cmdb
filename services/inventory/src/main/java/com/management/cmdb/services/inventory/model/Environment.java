package com.management.cmdb.services.inventory.model;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Environment {

    private String location;
    private EnvironmentType type;

    private String jiraTracker;
    private EnvironmentStatus status;

    public void checkIntegrity() {
        if (StringUtils.isBlank(location)) throw new InvalidObjectException("location cannot be blank", this);
        if (type == null) throw new InvalidObjectException("type cannot be null", this);
        if (status == null) throw new InvalidObjectException("status cannot be null", this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Environment that)) return false;
        return super.equals(that) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, type);
    }

    @Override
    public String toString() {
        return "Environment{" +
                "type=" + type +
                ", location='" + location + '\'' +
                ", status=" + status +
                '}';
    }
}
