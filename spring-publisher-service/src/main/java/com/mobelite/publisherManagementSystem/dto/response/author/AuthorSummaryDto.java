package com.mobelite.publisherManagementSystem.dto.response.author;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Data
@Builder
@Jacksonized
public class AuthorSummaryDto {
    private Long id;
    private String name;
    private String nationality;
    private LocalDate birthDate;
}
