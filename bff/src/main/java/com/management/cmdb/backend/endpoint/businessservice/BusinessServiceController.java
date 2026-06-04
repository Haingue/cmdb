package com.management.cmdb.backend.endpoint.businessservice;

import com.management.cmdb.backend.endpoint.businessservice.dto.BusinessServiceDto;
import com.management.cmdb.backend.endpoint.businessservice.mapper.BusinessServiceMapper;
import com.management.cmdb.backend.services.adapters.BusinessServiceAdapter;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.ports.inputs.BusinessServiceInputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/service/business-service")
public class BusinessServiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessServiceController.class);

    private final BusinessServiceInputPort businessServiceInputPort;
    private final InventoryServiceClient inventoryServiceClient;

    public BusinessServiceController(BusinessServiceInputPort businessServiceInputPort, InventoryServiceClient inventoryServiceClient) {
        this.businessServiceInputPort = businessServiceInputPort;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @GetMapping("/{abbreviation}")
    public ResponseEntity<BusinessServiceDto> findOne(@PathVariable String abbreviation) {
        LOGGER.trace("Find one business service: {}", abbreviation);
        PaginatedResponseDto<BusinessService> paginatedResponseDto = inventoryServiceClient.searchBusinessServiceByAttributeValue(BusinessServiceAdapter.ABBREVIATION_LABEL, abbreviation, BusinessServiceAdapter.ITEM_TYPE_LABEL);
        if (paginatedResponseDto == null || paginatedResponseDto.isEmpty()) return ResponseEntity.notFound().build();
        BusinessService firstResult = paginatedResponseDto.content().getFirst();
        return ResponseEntity.ok(BusinessServiceMapper.INSTANCE.toDto(firstResult));
    }

    @PostMapping
    public ResponseEntity<BusinessServiceDto> create(@RequestBody BusinessServiceDto newBusinessService) {
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
    public ResponseEntity<BusinessServiceDto> update(@RequestBody BusinessServiceDto newBusinessService) {
        LOGGER.info("Update business service: {}", newBusinessService);
        BusinessService coreModel = BusinessServiceMapper.INSTANCE.toCoreModel(newBusinessService);
        coreModel.checkIntegrity();
        BusinessService businessService = businessServiceInputPort.update(coreModel, User.UNKNONW);

        return Optional
                .of(businessService)
                .map(BusinessServiceMapper.INSTANCE::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/link")
    public ResponseEntity<LinkDto> createLink(@RequestBody LinkDto linkDto) {
        LOGGER.info("Create link: {}", linkDto);
        // TODO: Implement logic to create a link between Project and BusinessService
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/link/{uuid}")
    public ResponseEntity<Void> deleteLink(@PathVariable UUID uuid) {
        LOGGER.info("Delete link: {}", uuid);
        // TODO: Implement logic to delete a link between Project and BusinessService
        return ResponseEntity.ok().build();
    }

}
