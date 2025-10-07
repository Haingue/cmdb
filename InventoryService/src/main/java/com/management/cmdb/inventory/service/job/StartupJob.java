package com.management.cmdb.inventory.service.job;

import com.management.cartography.core.models.business.project.BusinessService;
import com.management.cartography.core.models.business.project.Project;
import com.management.cmdb.inventory.service.entity.AttributeTypeEntity;
import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import com.management.cmdb.inventory.service.model.UserDetail;
import com.management.cmdb.inventory.service.model.itemTypes.DefaultItemType;
import com.management.cmdb.inventory.service.repository.ItemTypeRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class StartupJob implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupJob.class);

    private final Environment env;

    private final ItemTypeRepository itemTypeRepository;

    public StartupJob(Environment env, ItemTypeRepository itemTypeRepository) {
        this.env = env;
        this.itemTypeRepository = itemTypeRepository;
    }

    @PostConstruct
    private void init() throws NoSuchFieldException, NoSuchMethodException {
        for (DefaultItemType defaultItemType : DefaultItemType.values()) {
            if (this.itemTypeRepository.findFirstByLabel(defaultItemType.itemType.getLabel()).isEmpty()) {
                this.itemTypeRepository.save(defaultItemType.itemType);
            }
        }

        // String packageName = "com.management.cartography.core.models.business";
        // Set<Class> coreModels = findAllClassesUsingClassLoader(packageName);
        Set<Class> coreModels = Set.of(
                BusinessService.class,
                Project.class,
                com.management.cartography.core.models.business.project.Environment.class,
                com.management.cartography.core.models.business.component.Component.class,
                com.management.cartography.core.models.business.component.Software.class,
                com.management.cartography.core.models.business.component.Hardware.class,
                com.management.cartography.core.models.business.component.Host.class,
                com.management.cartography.core.models.business.component.VirtualMachine.class
        );
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

            this.itemTypeRepository.save(itemTypeEntity);
        }
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
