package com.management.cmdb.services.aggregator.syslog.job;

import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import com.management.cmdb.services.aggregator.syslog.service.ItemService;
import com.management.cmdb.services.aggregator.syslog.service.SyslogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExportCachedTraffic {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportCachedTraffic.class);

    private final SyslogService syslogService;
    private final ItemService itemService;

    public ExportCachedTraffic(SyslogService syslogService, ItemService itemService) {
        this.syslogService = syslogService;
        this.itemService = itemService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void exportCollectedTraffics() {
        LOGGER.info("Exporting collected traffics to inventory");
        Map<String, Map<String, Traffic>> traffics = syslogService.getTraffics();
        for (Map.Entry<String, Map<String, Traffic>> source : traffics.entrySet()) {
            LOGGER.debug("Exporting traffics from {} [destination={}]", source.getKey(), source.getValue().size());
            try {
                for (Map.Entry<String, Traffic> destination : source.getValue().entrySet()) {
                    try {
                        Traffic traffic = destination.getValue().clone();
                        itemService.upsertItemLink(traffic);
                    } catch (Exception e) {
                        LOGGER.error("Failed to upsert item link", e);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to export traffics from {} to inventory", source.getKey(), e);
            }
        }
    }

}
