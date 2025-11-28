package com.management.cmdb.services.aggregator.repository_scrapper.model;

import java.util.Map;

public record ProjectRepository(
        Project project,
        String name,
        Source source,
        String url,
        String defaultBranch,
        Map<String, String> attributes
) {
}
