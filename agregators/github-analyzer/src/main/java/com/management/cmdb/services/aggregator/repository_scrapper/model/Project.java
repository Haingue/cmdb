package com.management.cmdb.services.aggregator.repository_scrapper.model;

import java.util.List;

public record Project (
        String name,
        String businessService,
        List<String> contacts
) {
}
