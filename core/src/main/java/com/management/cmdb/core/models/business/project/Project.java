package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.technical.Entity;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class Project extends Entity {

    private String fullName;
    private String shortName;
    private String description;
    private BusinessService businessService;

    private UserGroup maintainers;
    private UserGroup owners;

    private Set<Environment> environments;

    public static Project create(String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners) {
        return new Project(fullName, shortName, description, businessService, maintainers, owners);
    }

    public static Project load(Entity entity, String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners) {
        Project project = new Project(fullName, shortName, description, businessService, maintainers, owners);
        project.reloadEntity(entity);
        return project;
    }

    protected Project(String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners) {
        super();
        this.fullName = fullName;
        this.shortName = shortName;
        this.description = description;
        this.businessService = businessService;
        this.maintainers = maintainers;
        this.owners = owners;
        this.environments = new HashSet<>();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BusinessService getBusinessService() {
        return businessService;
    }

    public void setBusinessService(BusinessService businessService) {
        this.businessService = businessService;
    }

    public UserGroup getMaintainers() {
        return maintainers;
    }

    public void setMaintainers(UserGroup maintainers) {
        this.maintainers = maintainers;
    }

    public UserGroup getOwners() {
        return owners;
    }

    public void setOwners(UserGroup owners) {
        this.owners = owners;
    }

    public Set<Environment> getEnvironments() {
        return Set.copyOf(environments);
    }

    public void addEnvironment(Environment environment) {
        environments.add(environment);
    }

    public void removeEnvironment(Environment environment) {
        environments.add(environment);
    }

    public boolean isValid() {
        if (StringUtils.isBlank(fullName)) throw new InvalidObjectException("fullName cannot be blank", this);
        if (StringUtils.isBlank(shortName)) throw new InvalidObjectException("shortName cannot be blank", this);
        if (StringUtils.isBlank(description)) throw new InvalidObjectException("description cannot be blank", this);
        if (businessService == null || StringUtils.isBlank(businessService.name())) throw new InvalidObjectException("businessService is missing", this);
        if (owners == null) throw new InvalidObjectException("owners is missing", this);
        return true;
    }
}
