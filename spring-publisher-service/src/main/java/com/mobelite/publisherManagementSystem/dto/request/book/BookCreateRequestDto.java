package com.mobelite.publisherManagementSystem.dto.request.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * DTO for creating a new book.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCreateRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotNull(message = "Publication date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 20, message = "ISBN must be between 10 and 20 characters")
    private String isbn;

    @NotNull(message = "Author ID is required")
    private Long authorId;
}