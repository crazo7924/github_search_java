// src/test/java/com/example/github/search/service/GithubSearchServiceTest.java
package com.example.github.search.service;

import com.example.github.search.dto.GithubSearchResponse;
import com.example.github.search.dto.RepositoryItem;
import com.example.github.search.dto.SearchRequestBody;
import com.example.github.search.repository.SearchResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubSearchServiceTest {

    @Mock
    private RestTemplate githubRestTemplate;

    @Mock
    private SearchResultRepository repository;

    @InjectMocks
    private GithubSearchService githubSearchService;

    private SearchRequestBody searchRequestBody;

    @BeforeEach
    void setUp() {
        // A standard request body for happy-path tests
        searchRequestBody = new SearchRequestBody();
        searchRequestBody.setQuery("tetris");
        searchRequestBody.setLanguage("java");
        searchRequestBody.setSort("stars");
    }

    @Test
    void searchRepositories_Success_ShouldFetchAndSaveItems() {
        // Arrange
        String expectedUrl = "/search/repositories?q=tetris+language:java&sort=stars";
        RepositoryItem item = new RepositoryItem();
        item.setId(1L);
        item.setName("Tetris");

        List<RepositoryItem> items = Collections.singletonList(item);
        GithubSearchResponse mockResponse = new GithubSearchResponse(null);
        mockResponse.setItems(items);

        when(githubRestTemplate.getForObject(eq(expectedUrl), eq(GithubSearchResponse.class)))
                .thenReturn(mockResponse);

        // Act
        GithubSearchResponse actualResponse = githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        assertNotNull(actualResponse);
        assertFalse(actualResponse.getItems().isEmpty());
        assertEquals(1, actualResponse.getItems().size());
        assertEquals("Tetris", actualResponse.getItems().get(0).getName());

        verify(githubRestTemplate).getForObject(eq(expectedUrl), eq(GithubSearchResponse.class));
        verify(repository).saveAll(items);
    }

    @Test
    void searchRepositories_ApiReturnsEmptyList_ShouldNotSave() {
        // Arrange
        String expectedUrl = "/search/repositories?q=tetris+language:java&sort=stars";
        GithubSearchResponse mockResponse = new GithubSearchResponse(null);
        mockResponse.setItems(Collections.emptyList());

        when(githubRestTemplate.getForObject(eq(expectedUrl), eq(GithubSearchResponse.class)))
                .thenReturn(mockResponse);

        // Act
        GithubSearchResponse actualResponse = githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        assertNotNull(actualResponse);
        assertTrue(actualResponse.getItems().isEmpty());
        verify(repository, never()).saveAll(any());
    }

    @Test
    void searchRepositories_ApiReturnsResponseWithNullItems_ShouldNotSave() {
        // Arrange
        String expectedUrl = "/search/repositories?q=tetris+language:java&sort=stars";
        GithubSearchResponse mockResponse = new GithubSearchResponse(null);
        mockResponse.setItems(null); // Items list is null

        when(githubRestTemplate.getForObject(eq(expectedUrl), eq(GithubSearchResponse.class)))
                .thenReturn(mockResponse);

        // Act
        GithubSearchResponse actualResponse = githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        assertNotNull(actualResponse);
        assertNull(actualResponse.getItems());
        verify(repository, never()).saveAll(any());
    }

    @Test
    void searchRepositories_ApiReturnsNullResponse_ShouldNotSaveAndReturnNull() {
        // Arrange
        String expectedUrl = "/search/repositories?q=tetris+language:java&sort=stars";
        when(githubRestTemplate.getForObject(eq(expectedUrl), eq(GithubSearchResponse.class)))
                .thenReturn(null);

        // Act
        GithubSearchResponse actualResponse = githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        assertNull(actualResponse);
        verify(repository, never()).saveAll(any());
    }

    @Test
    void searchRepositories_ApiThrowsHttpClientErrorException_ShouldPropagateException() {
        // Arrange
        String expectedUrl = "/search/repositories?q=tetris+language:java&sort=stars";
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized");

        when(githubRestTemplate.getForObject(eq(expectedUrl), eq(GithubSearchResponse.class)))
                .thenThrow(exception);

        // Act & Assert
        HttpClientErrorException thrown = assertThrows(
                HttpClientErrorException.class,
                () -> githubSearchService.searchRepositories(searchRequestBody)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatusCode());
        verify(repository, never()).saveAll(any());
    }

    @Test
    void searchRepositories_ApiThrowsRuntimeException_ShouldPropagateException() {
        // Arrange
        String expectedUrl = "/search/repositories?q=tetris+language:java&sort=stars";
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(githubRestTemplate.getForObject(eq(expectedUrl), eq(GithubSearchResponse.class)))
                .thenThrow(exception);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> githubSearchService.searchRepositories(searchRequestBody));
        verify(repository, never()).saveAll(any());
    }

    @Test
    void searchRepositories_WithNullQuery_ShouldThrowIllegalArgumentException() {
        // Arrange
        searchRequestBody.setQuery(null);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> githubSearchService.searchRepositories(searchRequestBody)
        );

        assertEquals("Search query must not be null.", thrown.getMessage());
        verifyNoInteractions(githubRestTemplate, repository);
    }

    @Test
    void searchRepositories_WithEmptyQuery_ShouldThrowIllegalArgumentException() {
        // Arrange
        searchRequestBody.setQuery("");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> githubSearchService.searchRepositories(searchRequestBody)
        );

        assertEquals("Search query must not be empty.", thrown.getMessage());
        verifyNoInteractions(githubRestTemplate, repository);
    }

    @Test
    void searchRepositories_WithNullLanguage_ShouldBuildQueryCorrectly() {
        // Arrange
        searchRequestBody.setLanguage(null);
        String expectedUrl = "/search/repositories?q=tetris&sort=stars"; // No language parameter
        when(githubRestTemplate.getForObject(anyString(), any())).thenReturn(new GithubSearchResponse(null));

        // Act
        githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        verify(githubRestTemplate).getForObject(eq(expectedUrl), eq(GithubSearchResponse.class));
    }

    @Test
    void searchRepositories_WithNullSort_ShouldBuildQueryCorrectly() {
        // Arrange
        searchRequestBody.setSort(null);
        String expectedUrl = "/search/repositories?q=tetris+language:java"; // No sort parameter
        when(githubRestTemplate.getForObject(anyString(), any())).thenReturn(new GithubSearchResponse(null));

        // Act
        githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        verify(githubRestTemplate).getForObject(eq(expectedUrl), eq(GithubSearchResponse.class));
    }

    @Test
    void searchRepositories_WithEmptySort_ShouldBuildQueryCorrectly() {
        // Arrange
        searchRequestBody.setSort("");
        String expectedUrl = "/search/repositories?q=tetris+language:java"; // No sort parameter
        when(githubRestTemplate.getForObject(anyString(), any())).thenReturn(new GithubSearchResponse(null));

        // Act
        githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        verify(githubRestTemplate).getForObject(eq(expectedUrl), eq(GithubSearchResponse.class));
    }
}