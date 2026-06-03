package com.management.cmdb.backend.services.inventory.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.Environment;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserGroupItemDeserializer extends JsonDeserializer<UserGroup> implements ContextualDeserializer {

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        return new UserGroupItemDeserializer();
    }

    @Override
    public UserGroup deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ItemDto itemDto = deserializationContext.readValue(jsonParser, ItemDto.class);
        String name = itemDto.name();
        return new UserGroup(name, null, null, null, Set.of());
    }
}
