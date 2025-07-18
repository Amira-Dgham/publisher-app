package com.mobelite.publisherManagementSystem.mapper;

import com.mobelite.publisherManagementSystem.dto.request.author.AuthorRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.author.AuthorResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.author.AuthorSummaryDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Author;
import com.mobelite.publisherManagementSystem.entity.Book;
import com.mobelite.publisherManagementSystem.entity.Magazine;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AuthorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "magazines", ignore = true)
    Author toEntity(AuthorRequestDto authorRequestDto);

    @Mapping(target = "books", source = "books", qualifiedByName = "mapBooksToSummary")
    @Mapping(target = "magazines", source = "magazines", qualifiedByName = "mapMagazinesToSummary")
    AuthorResponseDto toResponseDto(Author author);

    AuthorSummaryDto toSummaryDto(Author author);

    @Mapping(target = "authorName", source = "author.name")
    BookSummaryResponseDto mapBookToSummary(Book book);

    MagazineSummaryResponseDto mapMagazineToSummary(Magazine magazine);

    @Named("mapBooksToSummary")
    default List<BookSummaryResponseDto> mapBooksToSummary(List<Book> books) {  // changed Set<Book> to List<Book>
        if (books == null || books.isEmpty()) return Collections.emptyList();

        return books.stream()
                .filter(book -> book != null)
                .map(this::mapBookToSummary)
                .collect(Collectors.toList());
    }

    @Named("mapMagazinesToSummary")
    default List<MagazineSummaryResponseDto> mapMagazinesToSummary(Set<Magazine> magazines) {
        if (magazines == null || magazines.isEmpty()) return Collections.emptyList();

        return magazines.stream()
                .filter(magazine -> magazine != null)
                .map(this::mapMagazineToSummary)
                .collect(Collectors.toList());
    }
}