package com.management.cmdb.services.aggregator.syslog.service;

import com.management.cmdb.services.aggregator.syslog.external.inventory.InventoryServiceClient;
import com.management.cmdb.services.aggregator.syslog.external.inventory.dto.*;
import com.management.cmdb.services.aggregator.syslog.external.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        final String newItemTypeIpAddressAttribute = "IpAddress";

        PaginatedResponseDto<ItemDto> sourceItemList = inventoryServiceClient.searchItemByAttribute(newItemTypeIpAddressAttribute, traffic.getSourceIp(), newItemType);
        PaginatedResponseDto<ItemDto> destinationItemList = inventoryServiceClient.searchItemByAttribute(newItemTypeIpAddressAttribute, traffic.getDestinationIp(), newItemType);
        ItemDto sourceItem;
        if (sourceItemList.isEmpty()) {
            AttributeDto ipHost = new AttributeDto(null, newItemTypeIpAddressAttribute, null, traffic.getSourceIp(), null, null, null, null);
            sourceItem = new ItemDto(null, traffic.getSourceIp(), newItemDescription, hostItemType, Set.of(ipHost), new HashSet<>(), new HashSet<>(), null, null, null, null);
            sourceItem = inventoryServiceClient.createItem(sourceItem).getBody();
        } else {
            sourceItem = sourceItemList.content().getFirst();
        }
        ItemDto destinationItem;
        if (destinationItemList.isEmpty()) {
            AttributeDto ipHost = new AttributeDto(null, newItemTypeIpAddressAttribute, null, traffic.getDestinationIp(), null, null, null, null);
            destinationItem = new ItemDto(null, traffic.getDestinationIp(), newItemDescription, hostItemType, Set.of(ipHost), new HashSet<>(), new HashSet<>(), null, null, null, null);
            destinationItem = inventoryServiceClient.createItem(destinationItem).getBody();
        } else {
            destinationItem = destinationItemList.content().getFirst();
        }
        traffic.setSourceUuid(sourceItem.uuid());
        traffic.setDestinationUuid(destinationItem.uuid());
        LinkDto linkDto = new LinkDto(communicateWithLinkType, sourceItem.uuid(), destinationItem.uuid(), traffic.toString());
        sourceItem.outgoingLinks().add(linkDto);

        inventoryServiceClient.updateItem(sourceItem);
    }

}
