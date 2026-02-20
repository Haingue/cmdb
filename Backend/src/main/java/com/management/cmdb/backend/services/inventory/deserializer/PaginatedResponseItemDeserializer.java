package com.management.cmdb.backend.services.inventory.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;

import java.io.IOException;
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
            contentType = deserializationContext.getContextualType();
        }
        contentType = deserializationContext.getTypeFactory().constructCollectionType(List.class, contentType);
        return new PaginatedResponseItemDeserializer(contentType);
    }

    @Override
    public PaginatedResponseDto<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        TreeNode treeNode = jsonParser.readValueAsTree();
        return new PaginatedResponseDto<>(
                deserializationContext.readValue(treeNode.get("content").traverse(jsonParser.getCodec()), contentType),
                deserializationContext.readValue(treeNode.get("pageNumber").traverse(), Integer.class),
                deserializationContext.readValue(treeNode.get("pageSize").traverse(), Integer.class),
                deserializationContext.readValue(treeNode.get("totalElements").traverse(), Long.class),
                deserializationContext.readValue(treeNode.get("totalPages").traverse(), Integer.class),
                deserializationContext.readValue(treeNode.get("last").traverse(), Boolean.class)
        );
    }

}
