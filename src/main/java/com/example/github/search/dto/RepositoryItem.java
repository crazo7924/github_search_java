package com.example.github.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "search_results")
public class RepositoryItem {

    @Id
    private long id;
    private String name;

    @Column(length = 1024)
    private String description;

    @Column(name = "owner_name")
    private String ownerName;


    /**
     * Helper method for Jackson to deserialize the nested "owner" object.
     * Jackson calls this method with the "owner" JSON object, and we extract the "login" field.
     * This cleanly separates the JSON mapping from the JPA entity field.
     *
     * @param owner A map representing the JSON "owner" object from the API response.
     */
    @JsonProperty("owner")
    private void unpackOwnerFromNestedObject(Map<String, Object> owner) {
        this.ownerName = (String) owner.get("login");
    }

    @JsonProperty("stargazers_count")
    private int stars;

    private String language;

    @JsonProperty("forks_count")
    private int forks;

    @JsonProperty("updated_at")
    private Date lastUpdated;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }
}
