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
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.component.network.Vlan;
import com.management.cmdb.core.models.business.constant.NetworkArea;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class VlanItemDeserializer extends JsonDeserializer<Vlan> implements ContextualDeserializer {

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        return new VlanItemDeserializer();
    }

    @Override
    public Vlan deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ItemDto itemDto = deserializationContext.readValue(jsonParser, ItemDto.class);

        Map<String, String> attributes = itemDto.attributes().stream()
            .filter(attributeDto -> attributeDto.getValue() != null)
            .collect(Collectors.toMap(
                AttributeDto::getLabel,
                AttributeDto::getValue
            ));

        String description = itemDto.description();
        String number = attributes.get("Number");
        String firewallUuid = attributes.get("Firewall");
        String networkArea = attributes.get("NetworkArea");
        String ipRange = attributes.get("IpRange");

        Vlan.VlanBuilder<?, ?> vlanBuilder = Vlan.builder()
                .description(description)
                .ipRange(ipRange);
        if (number != null) {
            vlanBuilder.number(Integer.parseInt(number));
        }
        if (firewallUuid != null) {
            vlanBuilder.firewall(Host.builder().uuid(UUID.fromString(firewallUuid)).build());
        }
        if (networkArea != null) {
            vlanBuilder.networkArea(NetworkArea.valueOf(networkArea));
        }
        return vlanBuilder.build();
    }
}
