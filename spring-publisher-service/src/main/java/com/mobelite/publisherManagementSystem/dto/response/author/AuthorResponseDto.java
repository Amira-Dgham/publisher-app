package com.mobelite.publisherManagementSystem.dto.response.author;

import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorResponseDto {

    /**
     * Unique identifier for the author.
     */
    private Long id;

    /**
     * Author's full name.
     */
    private String name;

    /**
     * Author's birth date.
     */
    private LocalDate birthDate;

    /**
     * Author's nationality.
     */
    private String nationality;

    /**
     * List of books written by this author.
     */
    private List<BookSummaryResponseDto> books;

    /**
     * List of magazines this author has contributed to.
     */
    private List<MagazineSummaryResponseDto> magazines;
}