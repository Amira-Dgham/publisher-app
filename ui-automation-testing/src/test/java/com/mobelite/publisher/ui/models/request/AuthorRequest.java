package com.mobelite.publisher.ui.models.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request model for Author operations in E2E testing.
 * Mirrors the structure of the Spring service AuthorRequestDto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorRequest {

    private String name;
    private LocalDate birthDate;
    private String nationality;

}