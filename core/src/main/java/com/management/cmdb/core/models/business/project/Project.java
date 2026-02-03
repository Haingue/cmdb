package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.technical.VersionedSavedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Project extends VersionedSavedEntity {

    private String fullName;
    private String shortName;
    private String description;
    private BusinessService businessService;

    private UserGroup maintainers;
    private UserGroup owners;

    @Builder.Default
    private Set<Environment> environments = new HashSet<>();

    public void addEnvironment(Environment environment) {
        environments.add(environment);
    }

    public void removeEnvironment(Environment environment) {
        environments.remove(environment);
    }

    public void checkIntegrity() {
        if (StringUtils.isBlank(fullName)) throw new InvalidObjectException("fullName cannot be blank", this);
        if (StringUtils.isBlank(shortName)) throw new InvalidObjectException("shortName cannot be blank", this);
        if (StringUtils.isBlank(description)) throw new InvalidObjectException("description cannot be blank", this);
        if (businessService == null || StringUtils.isBlank(businessService.getName())) throw new InvalidObjectException("businessService is missing", this);
        if (owners == null) throw new InvalidObjectException("owners is missing", this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Project project)) return false;
        return super.equals(project) && Objects.equals(shortName, project.shortName) && Objects.equals(businessService, project.businessService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), shortName, businessService);
    }
}
