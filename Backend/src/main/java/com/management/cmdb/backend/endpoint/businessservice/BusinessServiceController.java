package com.management.cmdb.backend.endpoint.businessservice;

import com.management.cmdb.backend.endpoint.businessservice.dto.BusinessServiceDto;
import com.management.cmdb.backend.endpoint.businessservice.mapper.BusinessServiceMapper;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.core.models.business.project.BusinessService;
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
    public ResponseEntity<BusinessService> createProject(@RequestBody BusinessServiceDto newBusinessService) {
        LOGGER.info("New business service: {}", newBusinessService);
        BusinessService coreModel = BusinessServiceMapper.INSTANCE.toCoreModel(newBusinessService);
        coreModel.checkIntegrity();
        Optional<BusinessService> result = inventoryServiceClient.createItem(coreModel);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
