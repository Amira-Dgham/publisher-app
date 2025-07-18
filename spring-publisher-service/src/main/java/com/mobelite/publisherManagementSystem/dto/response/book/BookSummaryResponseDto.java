package com.mobelite.publisherManagementSystem.dto.response.book;

import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationSummaryResponseDto;
import com.mobelite.publisherManagementSystem.enums.PublicationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Summary response DTO for Book entity.
 * Contains essential book information for list views.
 */
@Data
@SuperBuilder
@Jacksonized
@EqualsAndHashCode(callSuper = true)
public class BookSummaryResponseDto extends PublicationSummaryResponseDto {
    private String isbn;
    private String authorName;

    public BookSummaryResponseDto() {
        setType(PublicationType.BOOK);
    }
}