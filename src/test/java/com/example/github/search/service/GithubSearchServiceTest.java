package com.example.github.search.service;

import com.example.github.search.dto.GithubSearchResponse;
import com.example.github.search.dto.RepositoryItem;
import com.example.github.search.dto.SearchRequestBody;
import com.example.github.search.repository.SearchResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        // Common setup for search request body
        searchRequestBody = new SearchRequestBody();
        searchRequestBody.setQuery("tetris");
        searchRequestBody.setLanguage("java");
        searchRequestBody.setSort("stars");
    }

    @Test
    @DisplayName("getSavedRepositories should return all items from the database")
    void getSavedRepositories_shouldReturnAllItemsFromDatabase() {
        // Arrange
        RepositoryItem item1 = new RepositoryItem();
        item1.setId(1L);
        item1.setName("repo1");
        List<RepositoryItem> mockItems = List.of(item1);
        when(repository.findAll()).thenReturn(mockItems);

        // Act
        GithubSearchResponse response = githubSearchService.getSavedRepositories();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getItemCount());
        assertEquals(mockItems, response.getItems());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("getSavedRepositories should return an empty response when database is empty")
    void getSavedRepositories_shouldReturnEmptyResponseWhenDbIsEmpty() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GithubSearchResponse response = githubSearchService.getSavedRepositories();

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getItemCount());
        assertTrue(response.getItems().isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("searchRepositories should call GitHub API, save results, and return response")
    void searchRepositories_shouldCallApiAndSaveResults() {
        // Arrange
        RepositoryItem item1 = new RepositoryItem();
        item1.setId(1L);
        item1.setName("repo1");
        List<RepositoryItem> items = List.of(item1);

        GithubSearchResponse mockApiResponse = new GithubSearchResponse();
        mockApiResponse.setItems(items);
        mockApiResponse.setItemCount(1);

        String expectedUrl = "/search/repositories?q=tetris+language:java&sort=stars";
        when(githubRestTemplate.getForObject(eq(expectedUrl), eq(GithubSearchResponse.class)))
                .thenReturn(mockApiResponse);

        // Act
        GithubSearchResponse actualResponse = githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.getItemCount());
        assertEquals(items, actualResponse.getItems());

        // Verify that the API was called with the correct URL
        verify(githubRestTemplate, times(1)).getForObject(eq(expectedUrl), eq(GithubSearchResponse.class));
        // Verify that the results were saved to the repository
        verify(repository, times(1)).saveAll(items);
    }

    @Test
    @DisplayName("searchRepositories should build URL correctly without optional parameters")
    void searchRepositories_shouldBuildUrlCorrectlyWithoutOptionalParams() {
        // Arrange
        searchRequestBody.setLanguage(null);
        searchRequestBody.setSort(null);

        GithubSearchResponse mockApiResponse = new GithubSearchResponse();
        mockApiResponse.setItems(Collections.emptyList());

        String expectedUrl = "/search/repositories?q=tetris";
        when(githubRestTemplate.getForObject(eq(expectedUrl), eq(GithubSearchResponse.class)))
                .thenReturn(mockApiResponse);

        // Act
        githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        // Verify that the API was called with the correctly built URL
        verify(githubRestTemplate, times(1)).getForObject(eq(expectedUrl), eq(GithubSearchResponse.class));
        // Verify saveAll is not called for empty results
        verify(repository, never()).saveAll(any());
    }

    @Test
    @DisplayName("searchRepositories should not save to DB when API returns no items")
    void searchRepositories_shouldNotSaveWhenApiReturnsNoItems() {
        // Arrange
        GithubSearchResponse mockApiResponse = new GithubSearchResponse();
        mockApiResponse.setItems(Collections.emptyList());
        mockApiResponse.setItemCount(0);

        String expectedUrl = "/search/repositories?q=tetris+language:java&sort=stars";
        when(githubRestTemplate.getForObject(anyString(), eq(GithubSearchResponse.class)))
                .thenReturn(mockApiResponse);

        // Act
        githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        // Verify that saveAll was never called
        verify(repository, never()).saveAll(any());
    }

    @Test
    @DisplayName("searchRepositories should re-throw HttpClientErrorException on API error")
    void searchRepositories_shouldRethrowHttpClientErrorException() {
        // Arrange
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.FORBIDDEN, "API rate limit exceeded");
        when(githubRestTemplate.getForObject(anyString(), eq(GithubSearchResponse.class)))
                .thenThrow(exception);

        // Act & Assert
        HttpClientErrorException thrown = assertThrows(
                HttpClientErrorException.class,
                () -> githubSearchService.searchRepositories(searchRequestBody)
        );

        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatusCode());
        // Verify that no interaction with the repository happened
        verify(repository, never()).saveAll(any());
    }

    @Test
    @DisplayName("searchRepositories should throw IllegalArgumentException for null query")
    void searchRepositories_shouldThrowExceptionForNullQuery() {
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
    @DisplayName("searchRepositories should throw IllegalArgumentException for blank query")
    void searchRepositories_shouldThrowExceptionForBlankQuery() {
        // Arrange
        searchRequestBody.setQuery("   "); // Blank query

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


    @Test
    void searchRepositories_WithEmptyLanguage_ShouldBuildQueryCorrectly() {
        // Arrange
        searchRequestBody.setLanguage("");
        String expectedUrl = "/search/repositories?q=tetris&sort=stars"; // No language parameter
        when(githubRestTemplate.getForObject(anyString(), any())).thenReturn(new GithubSearchResponse(null));

        // Act
        githubSearchService.searchRepositories(searchRequestBody);

        // Assert
        verify(githubRestTemplate).getForObject(eq(expectedUrl), eq(GithubSearchResponse.class));
    }
}