package com.management.cmdb.backend.services.inventory;

import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Project;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "inventoryService", url = "${com.management.cmdb.services.inventory.url}")
//@CircuitBreaker(name = "microservice", fallbackMethod = "fallback")
public interface InventoryServiceClient {

    @GetMapping("/item/{uuid}")
    Host getOneHostItem(@PathVariable UUID uuid);
    @GetMapping("/item/{uuid}")
    Project getOneProjectItem(@PathVariable UUID uuid);
    @GetMapping("/item")
    PaginatedResponseDto<ItemDto> searchItems(@RequestParam String label, @RequestParam String itemType, @RequestParam int pageNumber, @RequestParam int pageSize);

    @PostMapping("/item")
    Optional<BusinessService> createItem(@RequestBody BusinessService businessService);
    @PostMapping("/item")
    Optional<Project> createItem(@RequestBody Project project);

    @GetMapping("/item-type")
    PaginatedResponseDto<ItemTypeDto> searchItemTypes(@RequestParam String label, @RequestParam int pageNumber, @RequestParam int pageSize);

    @GetMapping("/item/{uuid}")
    Optional<ItemDto> getOneItem(@PathVariable UUID uuid);
    @PostMapping("/item")
    Optional<ItemDto> createItem(@RequestBody ItemDto itemDto);
    @PutMapping("/item")
    Optional<ItemDto> updateItem(@RequestBody ItemDto itemDto);
    @DeleteMapping("/item/{uuid}")
    Optional<ItemDto> deleteItem(@PathVariable UUID uuid);

}
