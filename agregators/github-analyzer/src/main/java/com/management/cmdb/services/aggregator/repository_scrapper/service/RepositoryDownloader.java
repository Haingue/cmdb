package com.management.cmdb.services.aggregator.repository_scrapper.service;

import com.management.cmdb.services.aggregator.repository_scrapper.model.ProjectRepository;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.util.List;

public interface RepositoryDownloader {
    List<ProjectRepository> getAllRepository ();
}
