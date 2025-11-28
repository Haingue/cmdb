package com.management.cmdb.services.aggregator.repository_scrapper.adpaters.downloader.github;

import com.management.cmdb.services.aggregator.repository_scrapper.model.ProjectRepository;
import com.management.cmdb.services.aggregator.repository_scrapper.service.RepositoryDownloader;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GithubAdapter implements RepositoryDownloader {

    private final GithubProperties githubProperties;
    private final WebClient webClient;

    public GithubAdapter(GithubProperties githubProperties, WebClient.Builder webClientBuilder) {
        this.githubProperties = githubProperties;
        this.webClient = webClientBuilder
                .baseUrl("https://api.github.com")
                .defaultHeader("Authorization", "Bearer " + githubProperties.token)
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }

    @Override
    public List<ProjectRepository> getAllRepository() {
        List<ProjectRepository> repositories = webClient.get()
                .uri("/orgs/{org}/repos", githubProperties.organization)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    throw new RuntimeException("Github return a bad answer");
                })
                .bodyToFlux(ProjectRepository.class)
                .filter(repository -> repository.url().matches(githubProperties.repository_match_regex))
                .collectList()
                .block();
        return repositories
                .stream()
                .filter(repository -> repository.url().matches(githubProperties.repository_match_regex))
                .toList();
    }

}
