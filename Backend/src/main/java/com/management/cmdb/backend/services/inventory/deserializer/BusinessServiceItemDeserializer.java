package com.management.cmdb.backend.services.inventory.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.core.models.business.project.BusinessService;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class BusinessServiceItemDeserializer extends JsonDeserializer<BusinessService> implements ContextualDeserializer {

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        return new BusinessServiceItemDeserializer();
    }

    @Override
    public BusinessService deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode jsonNode = deserializationContext.readTree(jsonParser);
        ItemDto itemDto;
        if (jsonNode.has("content") && jsonNode.get("totalElements").intValue() > 0) {
            /// TODO bug here
            itemDto = deserializationContext.readValue(jsonNode.get("content").get(0).traverse(jsonParser.getCodec()), ItemDto.class);
        } else {
            itemDto = deserializationContext.readValue(jsonNode.traverse(), ItemDto.class);
        }

        Map<String, String> attributes = itemDto.attributes().stream()
            .filter(attributeDto -> attributeDto.getValue() != null)
            .collect(Collectors.toMap(
                AttributeDto::getLabel,
                AttributeDto::getValue
            ));

        String fullName = attributes.get("fullName");
        String shortName = attributes.get("shortName");

        BusinessService.BusinessServiceBuilder businessServiceBuilder = BusinessService.builder()
                .abbreviation(fullName)
                .name(shortName);
        return businessServiceBuilder.build();
    }
}
