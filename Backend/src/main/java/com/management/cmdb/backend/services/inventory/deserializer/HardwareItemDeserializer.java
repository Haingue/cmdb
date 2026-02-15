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
import com.management.cmdb.core.models.business.component.Hardware;
import com.management.cmdb.core.models.business.component.network.Vlan;
import com.management.cmdb.core.models.business.constant.ActiveDirectoryDomainName;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.NetworkArea;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HardwareItemDeserializer extends JsonDeserializer<Hardware> implements ContextualDeserializer {

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        return new HardwareItemDeserializer();
    }

    @Override
    public Hardware deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ItemDto itemDto = deserializationContext.readValue(jsonParser, ItemDto.class);

        Map<String, String> attributes = itemDto.attributes().stream()
            .filter(attributeDto -> attributeDto.getValue() != null)
            .collect(Collectors.toMap(
                AttributeDto::getLabel,
                AttributeDto::getValue
            ));
        ComponentType componentType = ComponentType.valueOfOrDefault(itemDto.type().label());
        UUID uuid = itemDto.uuid();
        String name = itemDto.name();
        String description = itemDto.description();
        String certificate = attributes.get("Certificate");
        String version = attributes.get("Version");
        String technology = attributes.get("Technology");
        String ipAddress = attributes.get("IpAddress");
        String vlan = attributes.get("Vlan");
        String domain = attributes.get("Domain");
        String networkArea = attributes.get("NetworkArea");
        String patchingDay = attributes.get("PatchingDay");

        Hardware.HardwareBuilder<?, ?> component = Hardware.builder()
                .uuid(uuid)
                .name(name)
                .description(description)
                .type(componentType)
                .certificate(certificate)
                .dns(attributes.get("Dns"))
                .macAddress(attributes.get("MacAddress"))
                .location(attributes.get("Location"))
                .creationDatetime(itemDto.createdDate());
        if (version != null) {
            component.version(Version.fromString(version));
        }
        if (technology != null) {
            component.technology(Technology.builder().name(technology).build());
        }
        if (ipAddress != null) {
            try {
                component.ipAddress(InetAddress.getByName(ipAddress));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        if (vlan != null) {
            component
                    .vlan(Vlan.builder().number(Integer.parseInt(vlan)).build());
        }
        if (domain != null) {
            component.domain(ActiveDirectoryDomainName.valueOf(domain));
        }
        if (networkArea != null) {
            component.networkArea(NetworkArea.valueOf(networkArea));
        }
        if (patchingDay != null) {
            component.patchingDay(DayOfWeek.valueOf(patchingDay));
        }
        return component.build();
    }
}
