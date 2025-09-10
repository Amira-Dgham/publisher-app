package com.mobelite.publisherManagementSystem.mapper;

import com.mobelite.publisherManagementSystem.dto.response.book.BookResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Publication;
import com.mobelite.publisherManagementSystem.entity.Book;
import com.mobelite.publisherManagementSystem.entity.Magazine;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface PublicationMapper {

    BookResponseDto bookToResponseDto(Book book);
    MagazineResponseDto magazineToResponseDto(Magazine magazine);

    BookSummaryResponseDto bookToSummaryDto(Book book);
    MagazineSummaryResponseDto magazineToSummaryDto(Magazine magazine);

    default PublicationResponseDto toResponseDto(Publication publication) {
        if (publication instanceof Book) {
            return bookToResponseDto((Book) publication);
        } else if (publication instanceof Magazine) {
            return magazineToResponseDto((Magazine) publication);
        }
        throw new IllegalArgumentException("Unknown publication type");
    }

    default PublicationSummaryResponseDto toSummaryResponseDto(Publication publication) {
        if (publication instanceof Book) {
            return bookToSummaryDto((Book) publication);
        } else if (publication instanceof Magazine) {
            return magazineToSummaryDto((Magazine) publication);
        }
        throw new IllegalArgumentException("Unknown publication type");
    }

}