package com.mobelite.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Simple DTO for creating a magazine in E2E tests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MagazineRequest {

    private String title;
    private int issueNumber;
    private String publicationDate; // Use String for simplicity in E2E tests
    private List<Long> authorIds;
}