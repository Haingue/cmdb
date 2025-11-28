package com.management.cmdb.services.aggregator.repository_scrapper.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("repository-scrapper.server")
public class AppProperties {

    public String local_storage_path = "temp/";

    public String getLocal_storage_path() {
        return local_storage_path;
    }

    public void setLocal_storage_path(String local_storage_path) {
        this.local_storage_path = local_storage_path;
    }
}
