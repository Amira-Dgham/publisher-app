package com.mobelite.publisherManagementSystem.dto.request.book;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Request DTO for updating an existing Book.
 * All fields are optional to support partial updates.
 */
@Data
@Builder
@Jacksonized
public class BookUpdateRequestDto {

    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$",
            message = "Invalid ISBN format")
    private String isbn;

    @Positive(message = "Author ID must be positive")
    private Long authorId;
}