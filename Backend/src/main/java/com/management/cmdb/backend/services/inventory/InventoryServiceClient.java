package com.management.cmdb.backend.services.inventory;

import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventoryService", url = "${com.management.cmdb.services.inventory.url}")
//@CircuitBreaker(name = "microservice", fallbackMethod = "fallback")
public interface InventoryServiceClient {

    @GetMapping("/item-type")
    PaginatedResponseDto<ItemTypeDto> searchItemTypes(@RequestParam String label, @RequestParam int pageNumber, @RequestParam int pageSize);
    @GetMapping("/item")
    PaginatedResponseDto<ItemTypeDto> searchItems(@RequestParam String label, @RequestParam String itemType, @RequestParam int pageNumber, @RequestParam int pageSize);

}
