package com.mobelite.publisherManagementSystem.dto.request.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for Author creation and update requests.
 * Contains validation constraints to ensure data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorRequestDto {

    /**
     * Author's full name.
     * Required field with length constraints.
     */
    @NotBlank(message = "Author name is required")
    @Size(min = 2, max = 100, message = "Author name must be between 2 and 100 characters")
    private String name;

    /**
     * Author's birth date.
     * Must be in the past.
     */
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    /**
     * Author's nationality.
     * Optional field with length constraints.
     */
    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;
}
