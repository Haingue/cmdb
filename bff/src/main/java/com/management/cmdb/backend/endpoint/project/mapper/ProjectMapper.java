package com.management.cmdb.backend.endpoint.project.mapper;

import com.management.cmdb.backend.endpoint.businessservice.mapper.BusinessServiceMapper;
import com.management.cmdb.backend.endpoint.environment.mapper.EnvironmentMapper;
import com.management.cmdb.backend.endpoint.project.dto.ProjectDto;
import com.management.cmdb.core.models.business.project.Project;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {EnvironmentMapper.class, BusinessServiceMapper.class})
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    ProjectDto toDto(Project coreModel);
    Project toCoreModel(ProjectDto dto);
}
