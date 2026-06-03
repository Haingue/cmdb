package com.management.cmdb.backend.endpoint.businessservice.mapper;

import com.management.cmdb.backend.endpoint.businessservice.dto.BusinessServiceDto;
import com.management.cmdb.core.models.business.project.BusinessService;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BusinessServiceMapper {

    BusinessServiceMapper INSTANCE = Mappers.getMapper(BusinessServiceMapper.class );

    BusinessServiceDto toDto(BusinessService coreModel);
    BusinessService toCoreModel(BusinessServiceDto dto);

}
