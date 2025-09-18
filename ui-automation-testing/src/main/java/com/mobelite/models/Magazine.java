package com.mobelite.publisher.ui.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Model representing a Magazine for E2E testing.
 * Mirrors the structure of the Spring service Magazine entity and DTOs.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Magazine {
    
    private Long id;
    private String title;
    private LocalDate publicationDate;
    private Integer issueNumber;
    private List<Author> authors;
} 