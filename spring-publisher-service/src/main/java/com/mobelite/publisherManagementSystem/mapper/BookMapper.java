package com.mobelite.publisherManagementSystem.mapper;

import com.mobelite.publisherManagementSystem.dto.request.book.BookCreateRequestDto;
import com.mobelite.publisherManagementSystem.dto.request.book.BookUpdateRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Book;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    Book toEntity(BookCreateRequestDto request);

    @Mapping(target = "author", source = "author")
    BookResponseDto toResponse(Book book);

    @Mapping(target = "authorName", source = "author.name")
    BookSummaryResponseDto toSummaryResponse(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(BookUpdateRequestDto request, @MappingTarget Book book);
}