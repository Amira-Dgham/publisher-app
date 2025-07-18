package com.mobelite.publisherManagementSystem.dto.response.magazine;

import com.mobelite.publisherManagementSystem.dto.response.author.AuthorSummaryDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Data Transfer Object for Magazine responses.
 * Contains complete magazine information including related entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "Response object containing complete magazine information")
public class MagazineResponseDto extends PublicationResponseDto {

    @Schema(description = "Magazine title", example = "National Geographic")
    private String title;

    @Schema(description = "Magazine issue number", example = "142")
    private Integer issueNumber;

    @Schema(description = "List of authors")
    private List<AuthorSummaryDto> authors;
}