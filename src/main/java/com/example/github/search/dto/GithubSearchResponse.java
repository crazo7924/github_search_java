package com.example.github.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GithubSearchResponse extends BaseResponse {

    @JsonProperty("total_count")
    private int totalCount;
    @JsonProperty("incomplete_results")
    private boolean incompleteResults;

    private List<RepositoryItem> items;

    public GithubSearchResponse(String message) {
        super(message);
    }

    public List<RepositoryItem> getItems() {
        return items;
    }

    public void setItems(List<RepositoryItem> items) {
        this.items = items;
    }
}