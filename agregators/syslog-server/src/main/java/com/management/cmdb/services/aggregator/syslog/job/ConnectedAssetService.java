package com.management.cmdb.services.aggregator.syslog.job;

import com.management.cmdb.services.aggregator.syslog.external.inventory.InventoryServiceClient;
import com.management.cmdb.services.aggregator.syslog.external.inventory.dto.*;
import com.management.cmdb.services.aggregator.syslog.external.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import com.management.cmdb.services.aggregator.syslog.service.SyslogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Service
public class ConnectedAssetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectedAssetService.class);

    private final String newItemType = "Host";
    private final ItemTypeDto hostItemType = new ItemTypeDto(null, newItemType, null, null, null, null, null, null);
    private final LinkTypeDto communicateWithLinkType = new LinkTypeDto("Communicate with");

    private final SyslogService syslogService;
    private final InventoryServiceClient inventoryServiceClient;

    public ConnectedAssetService(SyslogService syslogService, InventoryServiceClient inventoryServiceClient) {
        this.syslogService = syslogService;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Scheduled(cron = "10 * * * * *")
    public void exportCollectedTraffics() {
        LOGGER.debug("Exporting collected traffics to inventory");
        Map<String, Map<String, Traffic>> traffics = syslogService.getTraffics();
        for (Map.Entry<String, Map<String, Traffic>> source : traffics.entrySet()) {
            LOGGER.debug("Exporting traffics from {} [destination={}]", source.getKey(), source.getValue().size());
            try {
                for (Map.Entry<String, Traffic> destination : source.getValue().entrySet()) {
                    try {
                        Traffic traffic = destination.getValue().clone();
                        upsertItemLink(traffic);
                    } catch (Exception e) {
                        LOGGER.error("Failed to upsert item link", e);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to export traffics from {} to inventory", source.getKey(), e);
            }
        }
    }

    private void upsertItemLink (Traffic traffic) {
        final String newItemDescription = "New host detected by syslog";
        final String newItemTypeIpAddressAttribute = "ipAddress";
        final String newItemTypeHostnameAttribute = "hostname";

        PaginatedResponseDto<ItemDto> sourceItemList = inventoryServiceClient.searchItemByAttribute(newItemTypeIpAddressAttribute, traffic.getSourceIp(), newItemType);
        PaginatedResponseDto<ItemDto> destinationItemList = inventoryServiceClient.searchItemByAttribute(newItemTypeIpAddressAttribute, traffic.getDestinationIp(), newItemType);
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

        inventoryServiceClient.updateItem(sourceItem);
    }

    private ItemDto createNewItemDto(String traffic, String newItemTypeIpAddressAttribute, String newItemTypeHostnameAttribute, String newItemDescription) {
        String hostName = resolveHostname(traffic);
        AttributeDto ipHostAttribute = new AttributeDto(null, newItemTypeIpAddressAttribute, null, traffic, null, null, null, null);
        AttributeDto hostNameAttribute = new AttributeDto(null, newItemTypeHostnameAttribute, null, hostName, null, null, null, null);

        ItemDto newItemDto = new ItemDto(null, hostName, newItemDescription, hostItemType, Set.of(ipHostAttribute, hostNameAttribute), new HashSet<>(), new HashSet<>(), null, null, null, null);
        ResponseEntity<ItemDto> response = inventoryServiceClient.createItem(newItemDto);
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
