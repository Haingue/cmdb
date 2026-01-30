package com.management.cmdb.backend.endpoint.environment.mapper;

import com.management.cmdb.backend.endpoint.environment.dto.EnvironmentDto;
import com.management.cmdb.core.models.business.project.Environment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EnvironmentMapper {

    EnvironmentMapper INSTANCE = Mappers.getMapper(EnvironmentMapper.class);

    EnvironmentDto toDto(Environment coreModel);
    // TODO: Environment toCoreModel(EnvironmentDto dto);
}
