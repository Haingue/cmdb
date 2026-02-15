package com.management.cmdb.backend.services.inventory.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.core.models.business.project.BusinessService;

import java.io.IOException;
import java.util.Set;

public class BusinessServiceItemSerializer extends JsonSerializer<BusinessService> implements ContextualSerializer {
    public static final String BUSINESS_SERVICE = "BusinessService";

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        return new BusinessServiceItemSerializer();
    }

    @Override
    public void serialize(BusinessService businessService, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ItemTypeDto businessServiceItemType = new ItemTypeDto(null, BUSINESS_SERVICE,
                null, null, null, null, null, null);
        ItemDto businessServiceItem = new ItemDto(
                null,
                businessService.getName(),
                "Need to add description field",
                businessServiceItemType,
                Set.of(
                        new AttributeDto(null, "abbreviation", null, businessService.getAbbreviation(), null, null, null, null)
                ),
                Set.of(), Set.of(), null, null, null, null);
        jsonGenerator.writeObject(businessServiceItem);
    }
}
