package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.core.models.business.component.*;
import com.management.cmdb.core.models.business.component.network.Vlan;
import com.management.cmdb.core.models.business.constant.ActiveDirectoryDomainName;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.NetworkArea;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.ports.outputs.ComponentOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ComponentAdapter implements ComponentOutputPort {

    public static final String ITEM_TYPE_LABEL = "Component";

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentAdapter.class);

    private final InventoryServiceClient inventoryServiceClient;

    public ComponentAdapter(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Override
    public Optional<Component> findOne(UUID uuid) {
        Optional<ItemDto> result = inventoryServiceClient.getOneItem(uuid);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        ItemDto itemDto = result.get();

        Map<String, String> attributes = itemDto.attributes().stream().collect(Collectors.toMap(
                AttributeDto::getLabel,
                AttributeDto::getValue
        ));
        Component component;
        ComponentType componentType = ComponentType.valueOfOrDefault(itemDto.type().label());
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
        switch (itemDto.type().label()){
            case "Host":
                Host.HostBuilder<?, ?> hostBuilder = Host.builder()
                        .uuid(uuid)
                        .name(name)
                        .description(description)
                        .type(componentType)
                        .certificate(certificate)
                        .dns(attributes.get("Dns"))
                        .macAddress(attributes.get("MacAddress"));
                if (version != null) {
                    hostBuilder.version(Version.fromString(version));
                }
                if (technology != null) {
                    hostBuilder.technology(Technology.builder().name(technology).build());
                }
                if (version != null) {
                    hostBuilder.version(Version.fromString(version));
                }
                if (technology != null) {
                    hostBuilder.technology(Technology.builder().name(technology).build());
                }
                if (ipAddress != null) {
                    try {
                        hostBuilder.ipAddress(InetAddress.getByName(ipAddress));
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (vlan != null) {
                    hostBuilder
                            .vlan(Vlan.builder().number(Integer.parseInt(vlan)).build());
                }
                if (domain != null) {
                    hostBuilder.domain(ActiveDirectoryDomainName.valueOf(domain));
                }
                if (networkArea != null) {
                    hostBuilder.networkArea(NetworkArea.valueOf(networkArea));
                }
                if (patchingDay != null) {
                    hostBuilder.patchingDay(DayOfWeek.valueOf(patchingDay));
                }
                component = hostBuilder.build();
                break;
            case "Hardware":
                Hardware.HardwareBuilder<?, ?> hardwareBuilder = Hardware.builder()
                        .uuid(uuid)
                        .name(name)
                        .description(description)
                        .type(componentType)
                        .certificate(certificate)
                        .location(attributes.get("Location"))
                        .dns(attributes.get("Dns"))
                        .macAddress(attributes.get("MacAddress"));
                if (version != null) {
                    hardwareBuilder.version(Version.fromString(version));
                }
                if (technology != null) {
                    hardwareBuilder.technology(Technology.builder().name(technology).build());
                }
                if (ipAddress != null) {
                    try {
                        hardwareBuilder.ipAddress(InetAddress.getByName(ipAddress));
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (vlan != null) {
                    hardwareBuilder
                            .vlan(Vlan.builder().number(Integer.parseInt(vlan)).build());
                }
                if (domain != null) {
                    hardwareBuilder.domain(ActiveDirectoryDomainName.valueOf(domain));
                }
                if (networkArea != null) {
                    hardwareBuilder.networkArea(NetworkArea.valueOf(networkArea));
                }
                if (patchingDay != null) {
                    hardwareBuilder.patchingDay(DayOfWeek.valueOf(patchingDay));
                }
                component = hardwareBuilder.build();
                break;
            case "Software":
                Software.SoftwareBuilder<?, ?> softwareBuilder = Software.builder()
                        .uuid(uuid)
                        .name(name)
                        .description(description)
                        .type(componentType)
                        .certificate(certificate);
                if (version != null) {
                    softwareBuilder.version(Version.fromString(version));
                }
                if (technology != null) {
                    softwareBuilder.technology(Technology.builder().name(technology).build());
                }
                String hostName = attributes.get("Host");
                if (hostName != null) {
                    // TODO manage the list of host
                    softwareBuilder.hosts(Set.of(Host.builder().name(hostName).build()));
                }
                component = softwareBuilder.build();
                break;
            default:
                component = GenericComponent.builder()
                        .uuid(uuid)
                        .name(name)
                        .description(description)
                        .type(componentType)
                        .certificate(certificate)
                        .build();
                break;
        }
        return Optional.of(component);
    }

    @Override
    public Optional<Component> findOneByName(String name) {
        Optional<ItemDto> item = inventoryServiceClient.searchItems(name, ITEM_TYPE_LABEL, 0, 1)
                .content().stream().findFirst();
        return item.flatMap(itemDto -> findOne(itemDto.uuid()));
    }

    @Override
    public void delete(Component component) {
        inventoryServiceClient.deleteItem(component.getUuid());
    }
}
