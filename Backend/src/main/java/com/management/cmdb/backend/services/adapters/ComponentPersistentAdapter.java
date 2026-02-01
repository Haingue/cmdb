package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.core.models.business.component.*;
import com.management.cmdb.core.models.technical.ComponentVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ComponentPersistentAdapter implements ComponentVisitor<Component> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentAdapter.class);

    private final InventoryServiceClient inventoryServiceClient;

    public ComponentPersistentAdapter(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Override
    public Component accept(Component component) {
        return component.accept(this);
    }

    @Override
    public Component accept(Host host) {
        return null;
    }

    @Override
    public Component accept(Hardware hardware) {
        return null;
    }

    @Override
    public Component accept(Software software) {
        return null;
    }

    @Override
    public Component accept(VirtualMachine virtualMachine) {
        ItemTypeDto virtualMachineItemType = inventoryServiceClient.searchItemTypes("Virtual machine", 0, 1)
                .content()
                .getFirst();
        ItemDto reloadItem = Optional.of(inventoryServiceClient.searchItems(virtualMachine.getName(), virtualMachineItemType.label(), 0, 1)
                .content()
                .getFirst())
                .orElseGet(() -> inventoryServiceClient.createItem(
                                new ItemDto(null, virtualMachine.getName(), virtualMachine.getDescription(), virtualMachineItemType, Set.of(), Set.of(), Set.of(), null, null, null, null)
                        ).get());

        ItemDto itemDto = modelToItemDto(virtualMachine, reloadItem, virtualMachineItemType);
        inventoryServiceClient.updateItem(itemDto);
//        Set<AttributeDto> attributes = virtualMachineItemType.attributes()
//                .stream().map(attributeTypeDto -> {
//                    virtualMachine.accept(this);
//                });
//        inventoryServiceClient.getOneItem(virtualMachine.getUuid())
//                .orElse(new ItemDto(virtualMachine.getUuid(), virtualMachine.getName(), virtualMachine.getDescription(),
//                        virtualMachineItemType, ));
        return null;
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
                                        previousAttributeDto -> attribute.getKey().equalsIgnoreCase(previousAttributeDto.label()))
                                .findFirst()
                                .map(previousAttributeDto -> new AttributeDto(previousAttributeDto.uuid(), previousAttributeDto.label(), previousAttributeDto.attributeTypeId(), attribute.getValue(), previousAttributeDto.createdDate(), previousAttributeDto.createdBy(), previousAttributeDto.lastModifiedBy(), previousAttributeDto.lastModifiedDate()))
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
