package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.constant.GlobalStaticParameter;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.business.request.EnvironmentCreationRequest;
import com.management.cmdb.core.models.business.request.ProjectCreationRequest;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.inputs.ProjectInputPort;
import com.management.cmdb.core.ports.outputs.ProjectOutputPort;

import java.time.LocalDateTime;
import java.util.HashSet;
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
    public Project findOne(UUID uuid, User initiator) {
        // TODO check initiator permissions
        return this.projectOutputPort.findOne(uuid)
                .orElseThrow(() -> new NotFoundException("Project with id " + uuid.toString() + " not found"));
    }

    @Override
    public Project findOneByShortName(String shortName, User initiator) {
        // TODO check initiator permissions
        return this.projectOutputPort.findOneByShortName(shortName)
                .orElseThrow(() -> new NotFoundException(shortName));
    }

    public Project create(String fullName, String shortName, String description, BusinessService businessService, UserGroup maintainers, UserGroup owners, Set<Environment> environments, User initiator) {
        /* TODO check user authority and user group validation
        boolean requestMemberOfGroup = projectCreationRequest.getRequestor().userGroups().stream()
                .anyMatch(g -> g.name().equalsIgnoreCase(projectCreationRequest.getProject().getMaintainers().name()));
        */

        Project project = Project.builder()
                .fullName(fullName)
                .shortName(shortName)
                .description(description)
                .businessService(businessService)
                .maintainers(maintainers)
                .owners(owners)
                .build();
        project.checkIntegrity();

        BusinessService savedBusinessService = this.businessServiceService.findOne(project.getBusinessService().getName(), initiator);
        project.setBusinessService(savedBusinessService);
        project = this.projectOutputPort.save(project);

        if (environments != null && !environments.isEmpty()) {
            try {
                environments.stream()
                        .peek(environment -> environmentService.create(environment.getLocation(), environment.getType(), environment.getJiraTracker(), initiator))
                        .forEach(project::addEnvironment);
            } catch (CoreException e) {
                this.projectOutputPort.delete(project.getUuid());
                throw e;
            }
        }
        return this.projectOutputPort.save(project);
    }

    @Override
    public Project create(Project newEntity, User initiator) {
        return this.create(newEntity.getFullName(), newEntity.getShortName(), newEntity.getDescription(), newEntity.getBusinessService(), newEntity.getMaintainers(), newEntity.getOwners(), newEntity.getEnvironments(), initiator);
    }

    @Override
    public Project handleProjectCreationRequest(ProjectCreationRequest projectCreationRequest) {
        assert projectCreationRequest != null;
        assert projectCreationRequest.getProject() != null;
        assert projectCreationRequest.getRequestor() != null;

        if (projectCreationRequest.getBusinessServiceName() != null) {
            BusinessService businessService = businessServiceService.findOne(projectCreationRequest.getBusinessServiceName(), projectCreationRequest.getRequestor());
            projectCreationRequest.getProject().setBusinessService(businessService);
        }
        return this.create(projectCreationRequest.getProject(), projectCreationRequest.getRequestor());
    }

    @Override
    public Environment handleAddEnvironmentRequest (EnvironmentCreationRequest environmentCreationRequest) {
        assert environmentCreationRequest != null;
        assert environmentCreationRequest.getProjectId() != null;
        assert environmentCreationRequest.getEnvironment() != null;

        Project project = projectOutputPort.findOne(environmentCreationRequest.getProjectId())
                .orElseThrow(() -> new NotFoundException(environmentCreationRequest.getProjectId()));

        Environment environment = environmentCreationRequest.getEnvironment();
        project.addEnvironment(environment);
        environment = environmentService.create(environment, environmentCreationRequest.getRequestor());
        this.update(project, environmentCreationRequest.getRequestor());
        return environment;
    }

    @Override
    public Project update(Project project, User initiator) {
        if (project == null) throw new CoreException("Project cannot be null");
        if (project.getUuid() == null) throw new CoreException("Project uuid cannot be null");

        Project existingProject = this.findOne(project.getUuid(), initiator);
        existingProject.setFullName(project.getFullName());
        existingProject.setShortName(project.getShortName());
        existingProject.setDescription(project.getDescription());
        existingProject.setOwners(project.getOwners());
        // TODO notify new/previous owner
        existingProject.setMaintainers(project.getMaintainers());
        // TODO notify new/previous maintainer

        BusinessService businessService = this.businessServiceService.findOne(project.getBusinessService().getName(), initiator);
        existingProject.setBusinessService(businessService);

        project.getEnvironments()
                .stream().map((Environment env) -> this.environmentService.update(env, initiator))
                .forEach(existingProject::addEnvironment);
        existingProject.getEnvironments()
                .stream()
                .filter(environment -> !project.getEnvironments().contains(environment))
                .peek(environment -> this.environmentService.delete(environment.getUuid(), initiator))
                .forEach(existingProject::removeEnvironment);

        existingProject.update(GlobalStaticParameter.SYSTEM_NAME.name());

        project.checkIntegrity();
        return this.projectOutputPort.save(existingProject);
    }

    @Override
    public void archive(UUID uuid, User initiator) {
        Project project = this.findOne(uuid, initiator);
        LocalDateTime now = LocalDateTime.now();
        project.setArchiveDatetime(now);
        project.getEnvironments().forEach(environment -> this.environmentService.archive(environment.getUuid(), initiator));
        this.projectOutputPort.save(project);
    }

    @Override
    public void delete(UUID uuid, User initiator) {
        if (uuid == null) throw new CoreException("Project uuid cannot be null");

        Project existingProject = this.findOne(uuid, initiator);
        // TODO delete environments
        existingProject.getEnvironments().stream()
                .peek(environment -> this.environmentService.delete(environment.getUuid(), initiator))
                .forEach(existingProject::removeEnvironment);
        existingProject.archive(GlobalStaticParameter.SYSTEM_NAME.name());
        projectOutputPort.save(existingProject);
    }
}
