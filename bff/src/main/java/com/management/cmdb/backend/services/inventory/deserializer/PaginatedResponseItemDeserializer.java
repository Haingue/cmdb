package com.management.cmdb.backend.services.inventory.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaginatedResponseItemDeserializer extends JsonDeserializer<PaginatedResponseDto<?>> implements ContextualDeserializer {

    private JavaType contentType;

    public PaginatedResponseItemDeserializer() {
    }

    public PaginatedResponseItemDeserializer(JavaType contentType) {
        this.contentType = contentType;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        JavaType contentType;
        if (beanProperty != null) {
            contentType = beanProperty.getType().containedType(0);
        } else {
            contentType = deserializationContext.getContextualType().containedType(0);
        }
//        contentType = deserializationContext.getTypeFactory().constructCollectionType(List.class, contentType);
        return new PaginatedResponseItemDeserializer(contentType);
    }

    @Override
    public PaginatedResponseDto<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Integer pageNumber = deserializationContext.readTreeAsValue(node.get("pageNumber"), Integer.class);
        Integer pageSize = deserializationContext.readTreeAsValue(node.get("pageSize"), Integer.class);
        Long totalElements = deserializationContext.readTreeAsValue(node.get("totalElements"), Long.class);
        Integer totalPages = deserializationContext.readTreeAsValue(node.get("totalPages"), Integer.class);
        Boolean last = deserializationContext.readTreeAsValue(node.get("last"), Boolean.class);
        List<?> content = deserializationContext.readTreeAsValue(node.get("content"), TypeFactory.defaultInstance().constructParametricType(ArrayList.class, contentType));
        return new PaginatedResponseDto<>(
                content,
                pageNumber,
                pageSize,
                totalElements,
                totalPages,
                last
        );
    }

}
