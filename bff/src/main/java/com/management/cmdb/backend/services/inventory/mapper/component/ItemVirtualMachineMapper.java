package com.management.cmdb.backend.services.inventory.mapper.component;

import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.component.VirtualMachine;
import com.management.cmdb.core.models.business.component.network.Vlan;
import com.management.cmdb.core.models.business.constant.ActiveDirectoryDomainName;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.NetworkArea;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ItemVirtualMachineMapper implements ItemComponentMapper<VirtualMachine> {

    public static final ItemVirtualMachineMapper INSTANCE = new ItemVirtualMachineMapper();
    public static final String HOST_LINK_TYPE = "Hosted by";

    private ItemVirtualMachineMapper() {
    }

    @Override
    public VirtualMachine mapToCoreModel(ItemDto itemDto) {
        Map<String, String> attributes = itemDto.getMapAttributes();
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

        VirtualMachine.VirtualMachineBuilder<?, ?> component = VirtualMachine.builder()
                .uuid(uuid)
                .name(name)
                .description(description)
                .type(componentType)
                .certificate(certificate)
                .dns(attributes.get("Dns"))
                .macAddress(attributes.get("MacAddress"))
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

        // ESX host
        itemDto.outgoingLinks().stream()
                .filter(linkDto -> linkDto.linkType().label().equals(HOST_LINK_TYPE))
                .map(linkDto -> Host.builder()
                        .uuid(linkDto.targetItemId())
                        .build())
                .findFirst()
                .ifPresent(component::esx);
        return component.build();
    }

    @Override
    public ItemDto mapToItemDto(VirtualMachine component) {
        UUID uuid = component.getUuid();
        String name = component.getName();
        String description = component.getDescription();
        ItemTypeDto type = new ItemTypeDto(component.getType().name());

        Set<AttributeDto> attributes = new HashSet<>();

        String certificate = component.getCertificate();
        if (certificate != null) {
            attributes.add(AttributeDto.builder().label("Certificate").value(certificate).build());
        }
        Technology technology = component.getTechnology();
        if (technology != null) {
            attributes.add(AttributeDto.builder().label("Technology").value(technology.getName()).build());
        }
        Version version = component.getVersion();
        if (version != null) {
            attributes.add(AttributeDto.builder().label("Version").value(version.toString()).build());
        }
        InetAddress ipAddress = component.getIpAddress();
        if (ipAddress != null) {
            attributes.add(AttributeDto.builder().label("IpAddress").value(ipAddress.toString()).build());
        }
        Vlan vlan = component.getVlan();
        if (vlan != null) {
            attributes.add(AttributeDto.builder().label("Vlan").value(String.valueOf(vlan.getNumber())).build());
        }
        ActiveDirectoryDomainName domain = component.getDomain();
        if (domain != null) {
            attributes.add(AttributeDto.builder().label("Domain").value(domain.name()).build());
        }
        NetworkArea networkArea = component.getNetworkArea();
        if (networkArea != null) {
            attributes.add(AttributeDto.builder().label("NetworkArea").value(networkArea.name()).build());
        }
        DayOfWeek patchingDay = component.getPatchingDay();
        if (patchingDay != null) {
            attributes.add(AttributeDto.builder().label("PatchingDay").value(patchingDay.name()).build());
        }

        // ESX host


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
}
