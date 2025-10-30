package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.constants.GlobalStaticParameter;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.inputs.BusinessServiceInputPort;
import com.management.cmdb.core.ports.inputs.ProjectInputPort;
import com.management.cmdb.core.ports.outputs.BusinessServiceOutputPort;
import com.management.cmdb.core.ports.outputs.EnvironmentOutputPort;
import com.management.cmdb.core.ports.outputs.ProjectOutputPort;

import java.util.Set;
import java.util.UUID;

public class ProjectService implements ProjectInputPort {

    private final ProjectOutputPort projectOutputPort;
    private final BusinessServiceService businessServiceService;
    private final EnvironmentService environmentService;

    public ProjectService(ProjectOutputPort projectOutputPort, BusinessServiceService businessServiceService, EnvironmentService environmentService) {
        this.projectOutputPort = projectOutputPort;
        this.businessServiceService = businessServiceService;
        this.environmentService = environmentService;
    }

    @Override
    public Project create(String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners, Set<Environment> environments) {
        Project project = Project.create(fullName, shortName, description, businessService, maintainers, owners);
        project.isValid();

        BusinessService savedBusinessService = this.businessServiceService.findOneByName(project.getBusinessService().name());
        project.setBusinessService(savedBusinessService);
        project = this.projectOutputPort.save(project);

        if (environments != null && !environments.isEmpty()) {
            try {
                environments.stream()
                        .peek(environment -> environmentService.create(environment.getLocation(), environment.getType(), environment.getJiraTracker()))
                        .forEach(project::addEnvironment);
            } catch (CoreException e) {
                this.projectOutputPort.delete(project.getUuid());
                throw e;
            }
        }
        return this.projectOutputPort.save(project);
    }

    @Override
    public Project update(Project project) {
        if (project == null) throw new CoreException("Project cannot be null");
        if (project.getUuid() == null) throw new CoreException("Project uuid cannot be null");

        Project existingProject = this.projectOutputPort.findOne(project.getUuid())
                .orElseThrow(() -> new NotFoundException(project.getUuid()));
        existingProject.setFullName(project.getFullName());
        existingProject.setShortName(project.getShortName());
        existingProject.setDescription(project.getDescription());
        existingProject.setOwners(project.getOwners());
        // TODO notify new/previous owner
        existingProject.setMaintainers(project.getMaintainers());
        // TODO notify new/previous maintainer

        BusinessService businessService = this.businessServiceService.findOneByName(project.getBusinessService().name());
        existingProject.setBusinessService(businessService);

        project.getEnvironments()
                .stream().map(this.environmentService::update)
                .forEach(existingProject::addEnvironment);
        existingProject.getEnvironments()
                .stream()
                .filter(environment -> !project.getEnvironments().contains(environment))
                .peek(environment -> this.environmentService.delete(environment.getUuid()))
                .forEach(existingProject::removeEnvironment);

        existingProject.update(GlobalStaticParameter.SYSTEM_NAME.name());

        project.isValid();
        return this.projectOutputPort.save(existingProject);
    }

    @Override
    public void archive(UUID uuid) {
        throw new NotImplemented();
    }

    @Override
    public void delete(UUID uuid) {
        if (uuid == null) throw new CoreException("Project uuid cannot be null");

        Project existingProject = this.projectOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException(uuid));
        // TODO delete environments
        existingProject.getEnvironments().stream()
                .peek(environment -> this.environmentService.delete(environment.getUuid()))
                .forEach(existingProject::removeEnvironment);
        existingProject.delete(GlobalStaticParameter.SYSTEM_NAME.name());
        projectOutputPort.save(existingProject);
    }

    @Override
    public Project findOneByUuid(UUID uuid) {
        return this.projectOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException(uuid));
    }

    @Override
    public Project findOneByShortName(String shortName) {
        return this.projectOutputPort.findOneByShortName(shortName)
                .orElseThrow(() -> new NotFoundException(shortName));
    }
}
