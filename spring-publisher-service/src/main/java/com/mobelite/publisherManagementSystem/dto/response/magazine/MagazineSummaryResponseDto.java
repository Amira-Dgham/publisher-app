package com.mobelite.publisherManagementSystem.dto.response.magazine;

import com.mobelite.publisherManagementSystem.dto.response.author.AuthorSummaryDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationSummaryResponseDto;
import com.mobelite.publisherManagementSystem.enums.PublicationType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Data Transfer Object for Magazine summary information.
 * Used in lists and when detailed information is not required.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MagazineSummaryResponseDto extends PublicationSummaryResponseDto {
    private Integer issueNumber;
    private String title;
    private List<AuthorSummaryDto> authors;


    public MagazineSummaryResponseDto() {
        setType(PublicationType.MAGAZINE);
    }
}