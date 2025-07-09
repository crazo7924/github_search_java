// src/main/java/com/example/github/search/service/GithubSearchService.java
package com.example.github.search.service;

import com.example.github.search.dto.GithubSearchResponse;
import com.example.github.search.dto.RepositoryItem;
import com.example.github.search.dto.SearchRequestBody;
import com.example.github.search.repository.SearchResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class GithubSearchService {

    private static final Logger logger = LoggerFactory.getLogger(GithubSearchService.class);
    private final RestTemplate githubRestTemplate;
    private final SearchResultRepository repository;

    @Autowired
    public GithubSearchService(RestTemplate githubRestTemplate, SearchResultRepository repository) {
        this.githubRestTemplate = githubRestTemplate;
        this.repository = repository;
    }

    private String buildQuery(String query, String language) throws IllegalArgumentException {

        if (query == null) {
            throw new IllegalArgumentException("Search query must not be null.");
        }
        if (query.isBlank()) {
            throw new IllegalArgumentException("Search query must not be empty.");
        }

        if (language != null && !language.isBlank()) {
            return query + "+language:" + language;
        }

        return query;
    }

    /**
     * Searches repositories on GitHub using a blocking RestTemplate.
     *
     * @param body The request body containing the search parameters.
     * @return The search response.
     */
    public GithubSearchResponse searchRepositories(SearchRequestBody body) throws IllegalArgumentException {
        // Build the URL with query parameters safely
        String query = buildQuery(body.getQuery(), body.getLanguage());

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/search/repositories")
                .queryParam("q", query);

        String sortParam = body.getSort();
        if (sortParam != null && !sortParam.isBlank()) {
            builder.queryParam("sort", sortParam);
        }

        String url = builder.toUriString();

        logger.debug("Searching repositories with query='{}', sort='{}', language='{}'",
                body.getQuery(),
                body.getSort(),
                body.getLanguage());

        try {
            // Make the synchronous GET request
            GithubSearchResponse response = githubRestTemplate.getForObject(url, GithubSearchResponse.class);
            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                List<RepositoryItem> items = response.getItems();
                logger.info("Fetched {} repositories from GitHub API. Saving to database...", items.size());
                // Use the repository to save all fetched items in a single, efficient transaction
                repository.saveAll(items);
                logger.info("Successfully saved {} repositories to the database.", items.size());
            }

            return response;
        } catch (HttpClientErrorException e) {
            // Log the error and re-throw exception
            logger.error("Error during GitHub API call: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        }
    }
}