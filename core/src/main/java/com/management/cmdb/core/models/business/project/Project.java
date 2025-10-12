package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.technical.Entity;

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

    public static Project create (String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners) {
        return new Project(fullName, shortName, description, businessService, maintainers, owners);
    }

    public static Project load (Entity entity, String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners) {
        Project project = new Project(fullName, shortName, description, businessService, maintainers, owners);
        project.reloadEntity(entity);
        return project;
    }

    private Project(String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners) {
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

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public BusinessService getBusinessService() {
        return businessService;
    }

    public UserGroup getMaintainers() {
        return maintainers;
    }

    public UserGroup getOwners() {
        return owners;
    }

    public Set<Environment> getEnvironments() {
        return Set.copyOf(environments);
    }

    public void addEnvironment (Environment environment) {
        environments.add(environment);
    }
}
