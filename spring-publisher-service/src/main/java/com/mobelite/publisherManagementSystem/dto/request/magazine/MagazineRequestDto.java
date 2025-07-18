package com.mobelite.publisherManagementSystem.dto.request.magazine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for Magazine creation and update requests.
 * Contains validation annotations and Swagger documentation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for creating or updating a magazine")
public class MagazineRequestDto {

    @Schema(description = "Magazine title", example = "National Geographic")
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;

    @Schema(description = "Magazine issue number", example = "142")
    @NotNull(message = "Issue number cannot be null")
    @Min(value = 1, message = "Issue number must be at least 1")
    @Max(value = 99999, message = "Issue number cannot exceed 99999")
    private Integer issueNumber;

    @Schema(description = "List of author IDs", example = "[1, 2, 3]")
    @NotEmpty(message = "At least one author must be specified")
    private List<@NotNull(message = "Author ID cannot be null")
    @Min(value = 1, message = "Author ID must be positive") Long> authorIds;

    @Schema(description = "Publication date", example = "2025-01-01")
    @NotNull(message = "Publication date cannot be null")
    private LocalDate publicationDate;
}