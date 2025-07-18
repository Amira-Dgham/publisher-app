package com.mobelite.publisherManagementSystem.dto.response.publication;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Response DTO for publication details.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing publication details")
public class PublicationResponseDto {

    @Schema(description = "Publication ID", example = "1")
    private Long id;

    @Schema(description = "Title of the publication", example = "Spring Boot in Action")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Publication date", example = "2024-01-15")
    private LocalDate publicationDate;
}
