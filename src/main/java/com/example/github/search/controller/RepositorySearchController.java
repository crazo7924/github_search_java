package com.example.github.search.controller;

import com.example.github.search.dto.GithubSearchResponse;
import com.example.github.search.dto.SearchRequestBody;
import com.example.github.search.service.GithubSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
public class RepositorySearchController {

    private final GithubSearchService githubSearchService;

    @Autowired
    public RepositorySearchController(GithubSearchService githubSearchService) {
        this.githubSearchService = githubSearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<GithubSearchResponse> searchRepositories(
            @RequestBody SearchRequestBody body) {

        GithubSearchResponse response = githubSearchService.searchRepositories(body);
        response.setMessage("Repositories fetched and saved successfully");
        return ResponseEntity.ok(response);
    }
}