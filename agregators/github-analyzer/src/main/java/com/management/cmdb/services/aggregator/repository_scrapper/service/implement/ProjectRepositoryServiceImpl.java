package com.management.cmdb.services.aggregator.repository_scrapper.service.implement;

import com.management.cmdb.services.aggregator.repository_scrapper.model.ProjectRepository;
import com.management.cmdb.services.aggregator.repository_scrapper.model.RepositoryReport;
import com.management.cmdb.services.aggregator.repository_scrapper.properties.AppProperties;
import com.management.cmdb.services.aggregator.repository_scrapper.service.ProjectRepositoryService;
import com.management.cmdb.services.aggregator.repository_scrapper.service.RepositoryAnalyzer;
import com.management.cmdb.services.aggregator.repository_scrapper.service.RepositoryDownloader;
import jakarta.annotation.PostConstruct;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectRepositoryServiceImpl implements ProjectRepositoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRepositoryServiceImpl.class);

    private final AppProperties appProperties;
    private final RepositoryDownloader downloader;
    private final List<RepositoryAnalyzer> analyzers;

    public ProjectRepositoryServiceImpl(AppProperties appProperties, RepositoryDownloader downloader, List<RepositoryAnalyzer> analyzers) {
        this.appProperties = appProperties;
        this.downloader = downloader;
        this.analyzers = analyzers;
    }

    @PostConstruct
    void run () {
        LOGGER.info("Run project repository service");
        List<RepositoryReport> repositoryReports = analyzeAllRepositories();
        LOGGER.info("Results: {}", repositoryReports);
    }

    @Override
    public File cloneRepo(String repoUrl, String branch) throws GitAPIException {
        File localDir = new File(appProperties.local_storage_path + new File(repoUrl).getName());
        if (localDir.exists()) {
            return localDir;
        }
        Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(localDir)
                .setBranch(branch)
                .call();
        return localDir;
    }

    @Override
    public List<RepositoryReport> analyzeAllRepositories() {
        List<RepositoryReport> reports = new ArrayList<>();
        List<ProjectRepository> allRepository = this.downloader.getAllRepository();
        for (ProjectRepository projectRepository : allRepository) {
            try {
                File repositoryFolder = cloneRepo(projectRepository.url(), projectRepository.defaultBranch());
                RepositoryReport report = analyzeRepository(repositoryFolder);
                if (report != null) {
                    reports.add(report);
                }
            } catch (GitAPIException e) {
                LOGGER.error("Failed to clone repository: {}", projectRepository.url());
            }
        }
        return reports;
    }

    @Override
    public RepositoryReport analyzeRepository(File repositoryFolder) {
        for (RepositoryAnalyzer analyzer : analyzers) {
            RepositoryReport report = analyzer.analyze(repositoryFolder);
            if (report != null) {
                return report;
            }
        }
        return null;
    }
}
