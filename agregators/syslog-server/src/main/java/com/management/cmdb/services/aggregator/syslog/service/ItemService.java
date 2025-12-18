package com.management.cmdb.services.aggregator.syslog.service;

import com.management.cmdb.services.aggregator.syslog.external.inventory.InventoryServiceClient;
import com.management.cmdb.services.aggregator.syslog.external.inventory.dto.*;
import com.management.cmdb.services.aggregator.syslog.external.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

@Service
public class ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);

    private final String newItemType = "Host";
    private final ItemTypeDto hostItemType = new ItemTypeDto(null, newItemType, null, null, null, null, null, null);
    private final LinkTypeDto communicateWithLinkType = new LinkTypeDto("Communicate with");

    private final SyslogService syslogService;
    private final InventoryServiceClient inventoryServiceClient;

    public ItemService(SyslogService syslogService, InventoryServiceClient inventoryServiceClient) {
        this.syslogService = syslogService;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @EventListener
    public void trafficEvent (Traffic traffic) {
        // upsertItemLink(traffic);
    }

    public void upsertItemLink(Traffic traffic) {
        final String newItemDescription = "New host detected by syslog";
        final String newItemTypeIpAddressAttribute = "ipAddress";
        final String newItemTypeHostnameAttribute = "hostname";

        PaginatedResponseDto<ItemDto> sourceItemList = searchItemsByIpAddress(newItemTypeIpAddressAttribute, traffic.getSourceIp());
        PaginatedResponseDto<ItemDto> destinationItemList = searchItemsByIpAddress(newItemTypeIpAddressAttribute, traffic.getDestinationIp());
        ItemDto sourceItem;
        if (sourceItemList.isEmpty()) {
            sourceItem = createNewItemDto(traffic.getSourceIp(), newItemTypeIpAddressAttribute, newItemTypeHostnameAttribute, newItemDescription);
        } else {
            sourceItem = sourceItemList.content().getFirst();
        }
        ItemDto destinationItem;
        if (destinationItemList.isEmpty()) {
            destinationItem = createNewItemDto(traffic.getDestinationIp(), newItemTypeIpAddressAttribute, newItemTypeHostnameAttribute, newItemDescription);
        } else {
            destinationItem = destinationItemList.content().getFirst();
        }
        traffic.setSourceUuid(sourceItem.uuid());
        traffic.setDestinationUuid(destinationItem.uuid());
        LinkDto linkDto = new LinkDto(communicateWithLinkType, sourceItem.uuid(), destinationItem.uuid(), traffic.toString());
        sourceItem.outgoingLinks().add(linkDto);

        inventoryServiceClient.updateItem(sourceItem, newItemTypeIpAddressAttribute, traffic.getSourceIp());
    }

    public PaginatedResponseDto<ItemDto> searchItemsByIpAddress(String newItemTypeIpAddressAttribute, String ip) {
        return inventoryServiceClient.searchItemByAttribute(newItemTypeIpAddressAttribute, ip, newItemType);
    }

    private ItemDto createNewItemDto(String traffic, String newItemTypeIpAddressAttribute, String newItemTypeHostnameAttribute, String newItemDescription) {
        String hostName = resolveHostname(traffic);
        AttributeDto ipHostAttribute = new AttributeDto(null, newItemTypeIpAddressAttribute, null, traffic, null, null, null, null);
        AttributeDto hostNameAttribute = new AttributeDto(null, newItemTypeHostnameAttribute, null, hostName, null, null, null, null);

        ItemDto newItemDto = new ItemDto(null, hostName, newItemDescription, hostItemType, Set.of(ipHostAttribute, hostNameAttribute), new HashSet<>(), new HashSet<>(), null, null, null, null);
        ResponseEntity<ItemDto> response = inventoryServiceClient.createItem(newItemDto, ipHostAttribute.label(), ipHostAttribute.value());
        if (response.getStatusCode().is4xxClientError()) {
            LOGGER.error("Failed to create new item in inventory: {}", response.getBody());
            throw new RuntimeException("Failed to create new item in inventory");
        }
        newItemDto = response.getBody();
        return newItemDto;
    }

    public static String resolveHostname(String sourceIp) {
        try {
            InetAddress inetAddress = InetAddress.getByName(sourceIp);
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            return sourceIp;
        }
    }

}
