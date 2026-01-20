package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.technical.Event;
import com.management.cmdb.core.models.technical.VersionedSavedEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class Project extends VersionedSavedEntity {

    private String fullName;
    private String shortName;
    private String description;
    private BusinessService businessService;

    private UserGroup maintainers;
    private UserGroup owners;

    private Set<Environment> environments;

    public Project(String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners) {
        super();
        this.fullName = fullName;
        this.shortName = shortName;
        this.description = description;
        this.businessService = businessService;
        this.maintainers = maintainers;
        this.owners = owners;
        this.environments = new HashSet<>();
    }

    @Builder(buildMethodName = "reload")
    public Project (@NonNull UUID uuid, long revision, List<Event> events, LocalDateTime creationDatetime, LocalDateTime archiveDatetime,
                     String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners, Set<Environment> environments) {
        super(VersionedSavedEntity.reload(uuid, revision, events, creationDatetime, archiveDatetime));
        this.fullName = fullName;
        this.shortName = shortName;
        this.description = description;
        this.businessService = businessService;
        this.maintainers = maintainers;
        this.owners = owners;
        this.environments = environments;
    }

    public void addEnvironment(Environment environment) {
        environments.add(environment);
    }

    public void removeEnvironment(Environment environment) {
        environments.add(environment);
    }

    public void checkIntegrity() {
        if (StringUtils.isBlank(fullName)) throw new InvalidObjectException("fullName cannot be blank", this);
        if (StringUtils.isBlank(shortName)) throw new InvalidObjectException("shortName cannot be blank", this);
        if (StringUtils.isBlank(description)) throw new InvalidObjectException("description cannot be blank", this);
        if (businessService == null || StringUtils.isBlank(businessService.name())) throw new InvalidObjectException("businessService is missing", this);
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
