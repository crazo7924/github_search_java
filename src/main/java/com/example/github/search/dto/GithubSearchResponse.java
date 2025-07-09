package com.example.github.search.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public class GithubSearchResponse extends BaseResponse {

    @JsonProperty("total_count")
    private int itemCount;

    private List<RepositoryItem> items;

    public GithubSearchResponse(String message) {
        super(message);
    }

    public GithubSearchResponse() {
        super(null);
    }

    @JsonGetter("repositories")
    public List<RepositoryItem> getItems() {
        return items;
    }

    @JsonSetter("items")
    public void setItems(List<RepositoryItem> items) {
        this.items = items;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}