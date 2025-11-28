package com.management.cmdb.services.aggregator.repository_scrapper.service;

import com.management.cmdb.services.aggregator.repository_scrapper.model.RepositoryReport;

import java.io.File;

public interface RepositoryAnalyzer {

    RepositoryReport analyze (File repositoryFolder);

}
