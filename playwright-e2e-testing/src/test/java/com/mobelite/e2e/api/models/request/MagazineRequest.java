package com.mobelite.e2e.apis.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Request model for Magazine operations in E2E testing.
 * Mirrors the structure of the Spring service MagazineRequestDto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MagazineRequest {
    
    private String title;
    private LocalDate publicationDate;
    private Integer issueNumber;
    private List<Long> authorIds;

} 