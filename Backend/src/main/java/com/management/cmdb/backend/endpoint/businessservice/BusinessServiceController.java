package com.management.cmdb.backend.endpoint.businessservice;

import com.management.cmdb.backend.endpoint.businessservice.dto.BusinessServiceDto;
import com.management.cmdb.backend.endpoint.businessservice.mapper.BusinessServiceMapper;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.core.models.business.project.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/service/business-service")
public class BusinessServiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessServiceController.class);
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
