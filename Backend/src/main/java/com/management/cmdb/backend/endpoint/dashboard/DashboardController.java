package com.management.cmdb.backend.endpoint.dashboard;

import com.management.cmdb.backend.services.dashboard.DashboardMetrics;
import com.management.cmdb.backend.services.dashboard.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/service/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metrics")
    public Mono<DashboardMetrics> getMetrics() {
        return dashboardService.getMetrics();
    }
}
