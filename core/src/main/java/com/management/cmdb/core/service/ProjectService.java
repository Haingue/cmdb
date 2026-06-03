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
import com.management.cmdb.core.ports.inputs.BusinessServiceInputPort;
import com.management.cmdb.core.ports.inputs.EnvironmentInputPort;
import com.management.cmdb.core.ports.inputs.ProjectInputPort;
import com.management.cmdb.core.ports.outputs.ProjectOutputPort;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class ProjectService implements ProjectInputPort {

    private final ProjectOutputPort projectOutputPort;
    private final BusinessServiceInputPort businessServiceService;
    private final EnvironmentInputPort environmentService;

    public ProjectService(ProjectOutputPort projectOutputPort, BusinessServiceInputPort businessServiceInputPort, EnvironmentInputPort environmentInputPort) {
        this.projectOutputPort = projectOutputPort;
        this.businessServiceService = businessServiceInputPort;
        this.environmentService = environmentInputPort;
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
                .maintainers(maintainers)
                .owners(owners)
                .build();
        project.addBusinessService(businessService);
        project.checkIntegrity();

        BusinessService savedBusinessService = this.businessServiceService.findOne(businessService.getName(), initiator);
        project.removeBusinessService(businessService);
        project.addBusinessService(savedBusinessService);
        project = this.projectOutputPort.save(project);
// TODO remove this part ? (link create by children)
//        if (environments != null && !environments.isEmpty()) {
//            try {
//                // TODO save manually environment instead of save project (implicit -> explicit, no JPA/Hibernate)
//                environments.stream()
//                        .peek(environment -> environmentService.create(project.getUuid(), environment.getLocation(), environment.getType(), environment.getJiraTracker(), initiator))
//                        .forEach(project::addEnvironment);
//            } catch (CoreException e) {
//                this.projectOutputPort.delete(project.getUuid());
//                throw e;
//            }
//        }
        return this.projectOutputPort.save(project);
    }

    @Override
    public Project create(Project newEntity, User initiator) {
        BusinessService businessService = newEntity.getBusinessServices().stream().findFirst().orElse(null);
        return this.create(newEntity.getFullName(), newEntity.getShortName(), newEntity.getDescription(), businessService, newEntity.getMaintainers(), newEntity.getOwners(), newEntity.getEnvironments(), initiator);
    }

    @Override
    public Project handleProjectCreationRequest(ProjectCreationRequest projectCreationRequest) {
        assert projectCreationRequest != null;
        assert projectCreationRequest.getProject() != null;
        assert projectCreationRequest.getRequestor() != null;

        if (projectCreationRequest.getBusinessServiceName() != null) {
            BusinessService businessService = businessServiceService.findOne(projectCreationRequest.getBusinessServiceName(), projectCreationRequest.getRequestor());
            projectCreationRequest.getProject().addBusinessService(businessService);
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

        // Clear existing business services
        existingProject.getBusinessServices().clear();
        // Add updated business services
        project.getBusinessServices().forEach(businessService -> {
            BusinessService savedBusinessService = this.businessServiceService.findOne(businessService.getName(), initiator);
            existingProject.addBusinessService(savedBusinessService);
        });

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
