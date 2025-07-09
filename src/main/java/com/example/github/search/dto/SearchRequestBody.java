package com.example.github.search.dto;

/**
 * The body of our POST request
 */
public class SearchRequestBody {
    private String query;
    private String sort;
    private String language;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
