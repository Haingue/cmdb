package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.backend.services.inventory.dto.LinkDto;
import com.management.cmdb.core.models.business.component.*;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.technical.ComponentVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComponentPersistentAdapter implements ComponentVisitor<Component> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentAdapter.class);

    private final InventoryServiceClient inventoryServiceClient;

    public ComponentPersistentAdapter(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    private ItemDto componentToItemDto(Component component) {
        ItemTypeDto itemTypeDto = inventoryServiceClient.searchItemTypes(component.getType().name(), 0, 1)
                .content()
                .getFirst();
        UUID uuid = component.getUuid();
        String name = component.getName();
        String description = component.getDescription();
        ItemTypeDto type = itemTypeDto;
        Set<AttributeDto> attributes = new HashSet<>(Set.of(
                AttributeDto.builder().label("Type").value(component.getType().name()).build(),
                AttributeDto.builder().label("Certificate").value(component.getCertificate()).build(),
                AttributeDto.builder().label("Technology").value(component.getTechnology().getName()).build(),
                AttributeDto.builder().label("Version").value(component.getVersion().toString()).build()
        ));
        LocalDateTime createdDate = component.getCreationDatetime();
        UUID createdBy = User.UNKNONW.uuid();
        LocalDateTime lastModifiedDate = component.getCreationDatetime(); //TODO add modification datetime
        UUID lastModifiedBy = User.UNKNONW.uuid();

        return new ItemDto(
                uuid,
                name,
                description,
                type,
                attributes,
                null,
                null,
                createdDate,
                createdBy,
                lastModifiedBy,
                lastModifiedDate
        );
    }

    @Override
    public Component accept(Component component) {
        ItemDto itemDto = componentToItemDto(component);
        inventoryServiceClient.createItem(itemDto);
        return component;
    }

    @Override
    public Host accept(Host host) {
        ItemDto itemDto = componentToItemDto(host);
        itemDto.attributes().addAll(Set.of(
                AttributeDto.builder().label("Dns").value(host.getDns()).build(),
                AttributeDto.builder().label("Vlan").value(String.valueOf(host.getVlan().getNumber())).build(),
                AttributeDto.builder().label("MacAddress").value(host.getMacAddress()).build(),
                AttributeDto.builder().label("IpAddress").value(host.getIpAddress().toString()).build(),
                AttributeDto.builder().label("Domain").value(host.getDomain().name()).build(),
                AttributeDto.builder().label("NetworkArea").value(host.getNetworkArea().name()).build()
        ));
        inventoryServiceClient.createItem(itemDto);
        return host;
    }

    @Override
    public Hardware accept(Hardware hardware) {
        ItemDto itemDto = componentToItemDto(hardware);
        itemDto.attributes().addAll(Set.of(
                AttributeDto.builder().label("Dns").value(hardware.getDns()).build(),
                AttributeDto.builder().label("Vlan").value(String.valueOf(hardware.getVlan().getNumber())).build(),
                AttributeDto.builder().label("MacAddress").value(hardware.getMacAddress()).build(),
                AttributeDto.builder().label("IpAddress").value(hardware.getIpAddress().toString()).build(),
                AttributeDto.builder().label("Domain").value(hardware.getDomain().name()).build(),
                AttributeDto.builder().label("NetworkArea").value(hardware.getNetworkArea().name()).build(),
                AttributeDto.builder().label("Location").value(hardware.getLocation()).build()
        ));
        inventoryServiceClient.createItem(itemDto);
        return hardware;
    }

    @Override
    public Software accept(Software software) {
        ItemDto itemDto = componentToItemDto(software);
        itemDto.attributes().addAll(Set.of(
                // TODO manage the list of host
                AttributeDto.builder().label("Host").value(software.getHosts().stream().map(Host::getName).findFirst().orElse(null)).build()
        ));
        inventoryServiceClient.createItem(itemDto);
        return software;
    }

    @Override
    public VirtualMachine accept(VirtualMachine virtualMachine) {
        ItemDto itemDto = componentToItemDto(virtualMachine);
        itemDto.attributes().addAll(Set.of(
                AttributeDto.builder().label("Vlan").value(String.valueOf(virtualMachine.getVlan().getNumber())).build(),
                AttributeDto.builder().label("MacAddress").value(virtualMachine.getMacAddress()).build(),
                AttributeDto.builder().label("IpAddress").value(virtualMachine.getIpAddress().toString()).build(),
                AttributeDto.builder().label("Domain").value(virtualMachine.getDomain().name()).build(),
                AttributeDto.builder().label("NetworkArea").value(virtualMachine.getNetworkArea().name()).build(),
                AttributeDto.builder().label("Esx").value(virtualMachine.getEsx().getName()).build()
        ));
        inventoryServiceClient.createItem(itemDto);
        return virtualMachine;
    }

    private static <Model extends Component> ItemDto modelToItemDto (Model model, ItemDto previousItemDto, ItemTypeDto itemTypeDto) {
        Class<?> modelClass = model.getClass();
        Map<String, String> attributes;
        if (modelClass.isRecord()) {
            attributes = Arrays.stream(modelClass.getRecordComponents())
                    .distinct()
                    .filter(attribute -> !Arrays.asList("name", "description").contains(attribute.getName().toLowerCase()))
                    .collect(Collectors.toMap(
                            method -> method.getName().replaceAll("^get", ""),
                            method -> {
                                try {
                                    return method.getAccessor().invoke(model).toString();
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ));
        } else {
            attributes = Arrays.stream(modelClass.getMethods())
                    .filter(method -> method.getName().startsWith("get"))
                    .filter(method -> !method.getName().equals("getClass"))
                    .distinct()
                    .filter(attribute -> !Arrays.asList("name", "description").contains(attribute.getName().toLowerCase()))
                    .collect(Collectors.toMap(
                            method -> method.getName().replaceAll("^get", ""),
                            method -> {
                                try {
                                    return method.invoke(model).toString();
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ));
        }
        return new ItemDto(
                model.getUuid(),
                model.getName(),
                model.getDescription(),
                itemTypeDto,
                attributes.entrySet().stream()
                        .map(attribute -> previousItemDto.attributes().stream()
                                .filter(
                                        previousAttributeDto -> attribute.getKey().equalsIgnoreCase(previousAttributeDto.getLabel()))
                                .findFirst()
                                .map(previousAttributeDto -> new AttributeDto(previousAttributeDto.getUuid(), previousAttributeDto.getLabel(), previousAttributeDto.getAttributeTypeId(), attribute.getValue(), previousAttributeDto.getCreatedDate(), previousAttributeDto.getCreatedBy(), previousAttributeDto.getLastModifiedBy(), previousAttributeDto.getLastModifiedDate()))
                                .orElse(new AttributeDto(null, attribute.getKey(), null, attribute.getValue(), null, null, null, null)))
                        .collect(Collectors.toSet()),
                Set.of(),
                Set.of(),
                model.getCreationDatetime(),
                null,
                null,
                null
        );
    }
//    private static VirtualMachine itemDtoToModel (ItemDto itemDto) {
//        Class<?> modelClass = VirtualMachine.class;
//        modelClass.cons
//        Map<String, String> attributes;
//        if (modelClass.isRecord()) {
//            attributes = Arrays.stream(modelClass.getRecordComponents())
//                    .distinct()
//                    .filter(attribute -> !Arrays.asList("name", "description").contains(attribute.getName().toLowerCase()))
//                    .collect(Collectors.toMap(
//                            method -> method.getName().replaceAll("^get", ""),
//                            method -> {
//                                try {
//                                    return method.getAccessor().invoke(model).toString();
//                                } catch (IllegalAccessException | InvocationTargetException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            }
//                    ));
//        } else {
//            attributes = Arrays.stream(modelClass.getMethods())
//                    .filter(method -> method.getName().startsWith("set"))
//                    .filter(method -> !method.getName().equals("getClass"))
//                    .distinct()
//                    .filter(attribute -> !Arrays.asList("name", "description").contains(attribute.getName().toLowerCase()))
//                    .collect(Collectors.toMap(
//                            method -> method.getName().replaceAll("^set", ""),
//                            method -> {
//                                try {
//                                    return method.invoke(model).toString();
//                                } catch (IllegalAccessException | InvocationTargetException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            }
//                    ));
//        }
//        return new ItemDto(
//                model.getUuid(),
//                model.getName(),
//                model.getDescription(),
//                itemTypeDto,
//                attributes.entrySet().stream()
//                        .map(attribute -> previousItemDto.attributes().stream()
//                                .filter(
//                                        previousAttributeDto -> attribute.getKey().equalsIgnoreCase(previousAttributeDto.label()))
//                                .findFirst()
//                                .map(previousAttributeDto -> new AttributeDto(previousAttributeDto.uuid(), previousAttributeDto.label(), previousAttributeDto.attributeTypeId(), attribute.getValue(), previousAttributeDto.createdDate(), previousAttributeDto.createdBy(), previousAttributeDto.lastModifiedBy(), previousAttributeDto.lastModifiedDate()))
//                                .orElse(new AttributeDto(null, attribute.getKey(), null, attribute.getValue(), null, null, null, null)))
//                        .collect(Collectors.toSet()),
//                Set.of(),
//                Set.of(),
//                model.getCreationDatetime(),
//                null,
//                null,
//                null
//        );
//    }

//    private static Host ItemDto modelToItemDto (Host model, ItemDto reloadedItemDto, ItemTypeDto itemTypeDto) {
//        Map<String, AttributeDto> originalItemAttributes = reloadedItemDto.attributes().stream().collect(Collectors.toMap(
//                AttributeDto::label, attributeDto -> attributeDto
//        ));
//        Set<AttributeDto> attributes = new HashSet<>();
//        originalItemAttributes.get("Dns").;
//
//        return new ItemDto(
//                model.getUuid(),
//                model.getName(),
//                model.getDescription(),
//                itemTypeDto,
//                attributes,
//                Set.of(),
//                Set.of(),
//                model.getCreationDatetime(),
//                null,
//                null,
//                null
//        );
//    }

}
