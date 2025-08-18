package com.mobelite.e2e.apis.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Model representing a Publication for E2E testing.
 * Mirrors the structure of the Spring service Publication entity.
 * This is the base class for Book and Magazine.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Publication {
    
    private Long id;
    private String title;
    private LocalDate publicationDate;
    
} 