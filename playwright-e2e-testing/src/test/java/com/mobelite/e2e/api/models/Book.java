package com.mobelite.e2e.apis.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Model representing a Book for E2E testing.
 * Mirrors the structure of the Spring service Book entity and DTOs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Book {
    
    private Long id;
    private String title;
    private LocalDate publicationDate;
    private String isbn;
    private Author author;
} 