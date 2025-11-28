package com.management.cmdb.services.aggregator.repository_scrapper.service;

import com.management.cmdb.services.aggregator.repository_scrapper.model.ProjectRepository;
import com.management.cmdb.services.aggregator.repository_scrapper.model.RepositoryReport;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.util.List;

public interface ProjectRepositoryService {

    File cloneRepo(String repoUrl, String branch) throws GitAPIException;
    List<RepositoryReport> analyzeAllRepositories ();
    RepositoryReport analyzeRepository (File repositoryFolder);
}
