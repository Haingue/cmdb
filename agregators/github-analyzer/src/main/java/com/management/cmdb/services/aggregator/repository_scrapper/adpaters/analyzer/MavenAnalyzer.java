package com.management.cmdb.services.aggregator.repository_scrapper.adpaters.analyzer;

import com.management.cmdb.services.aggregator.repository_scrapper.model.RepositoryReport;
import com.management.cmdb.services.aggregator.repository_scrapper.service.RepositoryAnalyzer;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class MavenAnalyzer implements RepositoryAnalyzer {
    @Override
    public RepositoryReport analyze(File repositoryFolder) {

        return null;
    }
}
