package com.mobelite.publisherManagementSystem.dto.response.publication;
import com.mobelite.publisherManagementSystem.enums.PublicationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationSummaryResponseDto {
    private Long id;
    private String title;
    private LocalDate publicationDate;
    private PublicationType type;}