package com.management.cmdb.services.aggregator.syslog.controller;

import com.management.cmdb.services.aggregator.syslog.job.ExportCachedTraffic;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
public class SystemController {

    private final ExportCachedTraffic exportCachedTraffic;

    public SystemController(ExportCachedTraffic exportCachedTraffic) {
        this.exportCachedTraffic = exportCachedTraffic;
    }

    @GetMapping("/traffic-submit/force")
    public ResponseEntity<String> forceSending() {
        exportCachedTraffic.exportCollectedTraffics();
        return ResponseEntity.ok().build();
    }
}
