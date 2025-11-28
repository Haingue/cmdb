package com.management.cmdb.services.aggregator.repository_scrapper.adpaters.downloader.github;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("repository-scrapper.github")
public class GithubProperties {

    public final String base_url = "https://api.github.com";
    public String token = "";
    public String organization = "";
    public String repository_match_regex = ".*";

    public String getBase_url() {
        return base_url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRepository_match_regex() {
        return repository_match_regex;
    }

    public void setRepository_match_regex(String repository_match_regex) {
        this.repository_match_regex = repository_match_regex;
    }
}
