package com.management.cmdb.backend.controller.businessservice;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.business.request.ProjectCreationRequest;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/service/business-service")
public class BusinessServiceController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BusinessServiceController.class);
    public static final String BUSINESS_SERVICE = "BusinessService";

    private final InventoryServiceClient inventoryServiceClient;

    public BusinessServiceController(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createProject(@RequestBody BusinessService newBusinessService) {
        LOGGER.info("New business service: {}", newBusinessService);
        newBusinessService.checkIntegrity();
        ItemTypeDto businessServiceItemType = inventoryServiceClient.searchItemTypes(BUSINESS_SERVICE, 0, 1).content().getFirst();
        ItemDto businessServiceItem = new ItemDto(
                null,
                newBusinessService.name(),
                "Need to add description field",
                businessServiceItemType,
                Set.of(
                        new AttributeDto(null, "abbreviation", null, newBusinessService.abbreviation(), null, null, null, null)
                ),
                Set.of(), Set.of(), null, null, null, null);
        Optional<ItemDto> result = inventoryServiceClient.createItem(businessServiceItem);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
