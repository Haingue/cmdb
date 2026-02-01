package com.management.cmdb.backend.endpoint.component.dto;

import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;

import java.util.List;

public record AbstractComponentDto(
    String name,
    String description,
    ComponentType type,
    String certificate,
    Technology technology,
    Version version,
    List<Object> attributes
){
}
