package com.management.cmdb.services.inventory.job;

import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.services.inventory.entity.AttributeTypeEntity;
import com.management.cmdb.services.inventory.entity.ItemTypeEntity;
import com.management.cmdb.services.inventory.mapper.ItemTypeMapper;
import com.management.cmdb.services.inventory.model.UserDetail;
import com.management.cmdb.services.inventory.service.ItemTypeService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class StartupJob implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupJob.class);

    private final Environment env;

    private final ItemTypeService itemTypeService;

    public StartupJob(Environment env, ItemTypeService itemTypeService) {
        this.env = env;
        this.itemTypeService = itemTypeService;
    }

    @PostConstruct
    private void init() throws NoSuchFieldException, NoSuchMethodException {
        /*for (DefaultItemType defaultItemType : DefaultItemType.values()) {
            if (this.itemTypeService.findByLabel(defaultItemType.itemType.getLabel()).isEmpty()) {
                this.itemTypeService.create(ItemTypeMapper.INSTANCE.toDto(defaultItemType.itemType), UserDetail.SYSTEM);
            }
        }*/

        // String packageName = "com.management.cmdb.core.models.business";
        // Set<Class> coreModels = findAllClassesUsingClassLoader(packageName);
        Set<Class> coreModels = Set.of(
                BusinessService.class,
                Project.class,
                com.management.cmdb.core.models.business.project.Environment.class,
                com.management.cmdb.core.models.business.component.Component.class,
                com.management.cmdb.core.models.business.component.Software.class,
                com.management.cmdb.core.models.business.component.Hardware.class,
                com.management.cmdb.core.models.business.component.Host.class,
                com.management.cmdb.core.models.business.component.VirtualMachine.class
        );
        List<ItemTypeEntity> modelEntities = new ArrayList<>();
        for (Class coreModel : coreModels) {
            ItemTypeEntity itemTypeEntity = new ItemTypeEntity();
            itemTypeEntity.setUuid(UUID.randomUUID());
            itemTypeEntity.setLabel(coreModel.getSimpleName());
            itemTypeEntity.setDescription(
                    String.format("Description of %s", coreModel.getSimpleName()));
            itemTypeEntity.setCreatedBy(UserDetail.SYSTEM.uuid());

            // Add attribute type
            List<String> attributes;
            if (coreModel.isRecord()) {
                attributes = Arrays.stream(coreModel.getRecordComponents())
                        .map(recordComponent -> recordComponent.getName())
                        .toList();
            } else {
                attributes = Arrays.stream(coreModel.getMethods())
                        .filter(method -> method.getName().startsWith("get"))
                        .filter(method -> !method.getName().equals("getClass"))
                        .map(method -> method.getName().replaceAll("^get", ""))
                        .toList();
            }
            for (String attribute : attributes) {
                AttributeTypeEntity attributeType = new AttributeTypeEntity();
                attributeType.setUuid(UUID.randomUUID());
                attributeType.setLabel(attribute);
                attributeType.setDescription(String.format("Description of %s", attribute));
                attributeType.setShortDescription(String.format("Shortest description of %s", attribute));
                attributeType.setCreatedBy(UserDetail.SYSTEM.uuid());

                itemTypeEntity.getAttributes().add(attributeType);
            }
            modelEntities.add(itemTypeEntity);
        }

        modelEntities.stream().map(ItemTypeMapper.INSTANCE::toDto)
                .map(itemTypeDto -> itemTypeService.create(itemTypeDto, UserDetail.SYSTEM));
    }

    public Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Starting up application");
        LOGGER.info("Profiles: {}", Arrays.toString(env.getActiveProfiles()));
        LOGGER.info("App version: {}", env.getProperty("spring.application.version"));
        LOGGER.info("Application started");
    }
}
