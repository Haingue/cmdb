package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.backend.exceptions.AdapterException;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.outputs.BusinessServiceOutputPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BusinessServiceAdapter implements BusinessServiceOutputPort {

    public static final String ITEM_TYPE_LABEL = "BusinessService";
    public static final String ABBREVIATION_LABEL = "Abbreviation";

    private final InventoryServiceClient inventoryServiceClient;

    public BusinessServiceAdapter(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Override
    public Optional<BusinessService> findOne(String name) {
        PaginatedResponseDto<BusinessService> paginatedResponseDto = inventoryServiceClient.searchBusinessServiceByName(name, BusinessServiceAdapter.ITEM_TYPE_LABEL);
        return paginatedResponseDto.content().stream().findFirst();
    }

    @Override
    public Optional<BusinessService> findByAbbreviation(String abbreviation) {
        PaginatedResponseDto<BusinessService> paginatedResponseDto = this.inventoryServiceClient.searchBusinessServiceByAttributeValue(ABBREVIATION_LABEL, abbreviation, BusinessServiceAdapter.ITEM_TYPE_LABEL);
        return paginatedResponseDto.content().stream().findFirst();
    }

    @Override
    public BusinessService save(BusinessService businessService) {
        Optional<BusinessService> existingBusinessService = findOne(businessService.getName());
        if (existingBusinessService.isEmpty()) {
            return inventoryServiceClient.createItem(businessService)
                    .orElseThrow(AdapterException::new);
        } else {
            // TODO need to add item uuid
            return inventoryServiceClient.updateItem(businessService)
                    .orElseThrow(AdapterException::new);
        }
    }

    @Override
    public void delete(String s) {
        throw new NotImplemented();
    }
}
