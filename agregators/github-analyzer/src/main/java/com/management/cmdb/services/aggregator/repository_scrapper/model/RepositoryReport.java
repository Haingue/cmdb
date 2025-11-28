package com.management.cmdb.services.aggregator.repository_scrapper.model;

import java.util.List;

public record RepositoryReport(
        String repositoryName,
        String url,
        String stackStatus,
        List<String> stack,
        boolean isMonoRepository,
        int vulnerabilityNumber
) {
}
