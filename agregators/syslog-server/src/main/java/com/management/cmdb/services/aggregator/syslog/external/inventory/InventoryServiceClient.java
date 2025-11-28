package com.management.cmdb.services.aggregator.syslog.external.inventory;

import com.management.cmdb.services.aggregator.syslog.external.inventory.dto.ItemDto;
import com.management.cmdb.services.aggregator.syslog.external.inventory.dto.wrapper.PaginatedResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventoryService", url = "${services.inventory.url}")
//@CircuitBreaker(name = "microservice", fallbackMethod = "fallback")
public interface InventoryServiceClient {

    @GetMapping("/item/any/{attributeName}/{attributeValue}")
    PaginatedResponseDto<ItemDto> searchItemByAttribute(@PathVariable String attributeName,
                                                        @PathVariable String attributeValue,
                                                        @RequestParam String itemType);

    @PostMapping("/item")
    ResponseEntity<ItemDto> createItem(@RequestBody ItemDto itemDto);

    @PutMapping("/item")
    ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto);

}
