package com.mobelite.e2e.api.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request model for Book operations in E2E testing.
 * Mirrors the structure of the Spring service BookCreateRequestDto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookRequest {
    
    private String title;
    private LocalDate publicationDate;
    private String isbn;
    private Long authorId;

} 