package com.mobelite.publisherManagementSystem.dto.response.publication;

import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupedPublicationsResponse {
    private List<BookSummaryResponseDto> books;
    private List<MagazineSummaryResponseDto> magazines;
}