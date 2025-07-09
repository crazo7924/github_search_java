package com.example.github.search.repository;

import com.example.github.search.dto.RepositoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchResultRepository extends JpaRepository<RepositoryItem, Long> {
}
