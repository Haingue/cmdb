package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.backend.exceptions.AdapterException;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Project;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.outputs.BusinessServiceOutputPort;

import java.util.Optional;

public class BusinessServiceAdapter implements BusinessServiceOutputPort {

    public static final String ITEM_TYPE_LABEL = "BusinessService";

    private final InventoryServiceClient inventoryServiceClient;

    public BusinessServiceAdapter(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Override
    public Optional<BusinessService> findOne(String name) {
        return inventoryServiceClient.getOneBusinessServiceItem(name);
    }

    @Override
    public Optional<BusinessService> findByAbbreviation(String s) {
        throw new NotImplemented();
    }

    @Override
    public BusinessService save(BusinessService businessService) {
        Optional<BusinessService> newItem = inventoryServiceClient.createItem(businessService);
        if (newItem.isEmpty()) {
            return inventoryServiceClient.updateItem(businessService)
                    .orElseThrow(AdapterException::new);
        }
        return newItem
                .orElseThrow(AdapterException::new);
    }

    @Override
    public void delete(String s) {
        throw new NotImplemented();
    }
}
