package com.management.cmdb.backend.endpoint.businessservice;

import com.management.cmdb.backend.endpoint.businessservice.dto.BusinessServiceDto;
import com.management.cmdb.backend.endpoint.businessservice.mapper.BusinessServiceMapper;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.core.models.business.project.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/service/business-service")
public class BusinessServiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessServiceController.class);

    private final InventoryServiceClient inventoryServiceClient;

    public BusinessServiceController(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @PostMapping
    public ResponseEntity<BusinessServiceDto> createProject(@RequestBody BusinessServiceDto newBusinessService) {
        LOGGER.info("New business service: {}", newBusinessService);
        BusinessService coreModel = BusinessServiceMapper.INSTANCE.toCoreModel(newBusinessService);
        coreModel.checkIntegrity();
        Optional<BusinessService> result = inventoryServiceClient.createItem(coreModel);
        return result
                .map(BusinessServiceMapper.INSTANCE::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping
    public ResponseEntity<BusinessServiceDto> updateProject(@RequestBody BusinessServiceDto newBusinessService) {
        LOGGER.info("Update business service: {}", newBusinessService);
        BusinessService coreModel = BusinessServiceMapper.INSTANCE.toCoreModel(newBusinessService);
        coreModel.checkIntegrity();
        Optional<BusinessService> result = inventoryServiceClient.updateItem(coreModel);
        return result
                .map(BusinessServiceMapper.INSTANCE::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
