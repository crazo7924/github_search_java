// src/main/java/com/example/github/search/config/RestTemplateConfig.java
package com.example.github.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${github.api.base-url}")
    private String githubApiBaseUrl;

    @Value("${github.api.token}")
    private String githubApiToken;

    @Bean
    public RestTemplate githubRestTemplate(RestTemplateBuilder builder) {
        // Use the builder to configure the RestTemplate
        RestTemplateBuilder configuredBuilder = builder
                .rootUri(githubApiBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json");

        // Add Authorization header only if a token is present and not empty
        if (githubApiToken != null && !githubApiToken.isBlank()) {
            configuredBuilder = configuredBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubApiToken);
        }

        return configuredBuilder.build();
    }
}