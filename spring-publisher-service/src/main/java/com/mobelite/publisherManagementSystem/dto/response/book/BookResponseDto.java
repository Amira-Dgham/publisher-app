package com.mobelite.publisherManagementSystem.dto.response.book;

import com.mobelite.publisherManagementSystem.dto.response.author.AuthorSummaryDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Data Transfer Object for Book responses.
 */
@Data
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto extends PublicationResponseDto {
    private String isbn;
    private AuthorSummaryDto author;
}
