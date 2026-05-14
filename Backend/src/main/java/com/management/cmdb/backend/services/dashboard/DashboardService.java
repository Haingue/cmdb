package com.management.cmdb.backend.services.dashboard;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardService.class);

    private final InventoryServiceClient inventoryServiceClient;

    public DashboardService(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public Mono<DashboardMetrics> getMetrics() {
        try {
            // Récupération des counts depuis l'InventoryService
            // Servers = Host items
            long serverCount = countItemsByType("Host");
            
            // Applications = Software items
            long applicationCount = countItemsByType("Software");
            
            // Projects
            long projectCount = countItemsByType("Project");
            
            // Active users - à implémenter quand l'endpoint sera disponible
            long activeUserCount = 0L;

            LOGGER.info("Dashboard metrics fetched: servers={}, applications={}, projects={}, activeUsers={}", 
                serverCount, applicationCount, projectCount, activeUserCount);

            return Mono.just(new DashboardMetrics(serverCount, applicationCount, projectCount, activeUserCount));
        } catch (Exception e) {
            LOGGER.error("Error fetching dashboard metrics", e);
            // Retourne des valeurs par défaut en cas d'erreur
            return Mono.just(new DashboardMetrics(0L, 0L, 0L, 0L));
        }
    }

    private long countItemsByType(String itemType) {
        try {
            PaginatedResponseDto<ItemDto> response = inventoryServiceClient.searchItems("", itemType, 0, 1);
            return response.total();
        } catch (Exception e) {
            LOGGER.warn("Error counting items for type {}: {}", itemType, e.getMessage());
            return 0L;
        }
    }
}
