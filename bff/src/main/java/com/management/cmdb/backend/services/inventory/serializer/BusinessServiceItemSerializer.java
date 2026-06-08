package com.management.cmdb.backend.services.inventory.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.management.cmdb.backend.services.adapters.BusinessServiceAdapter;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.core.models.business.project.BusinessService;

import java.io.IOException;
import java.util.Set;

public class BusinessServiceItemSerializer extends JsonSerializer<BusinessService> implements ContextualSerializer {

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        return new BusinessServiceItemSerializer();
    }

    @Override
    public void serialize(BusinessService businessService, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ItemTypeDto businessServiceItemType = new ItemTypeDto(null, BusinessServiceAdapter.ITEM_TYPE_LABEL,
                null, null, null, null, null, null);
        ItemDto businessServiceItem = new ItemDto(
                null,
                businessService.getName(),
                "Need to add description field",
                businessServiceItemType,
                Set.of(
                        new AttributeDto(null, BusinessServiceAdapter.ABBREVIATION_LABEL, null, businessService.getAbbreviation(), null, null, null, null)
                ),
                Set.of(), Set.of(), null, null, null, null);
        jsonGenerator.writeObject(businessServiceItem);
    }
}
