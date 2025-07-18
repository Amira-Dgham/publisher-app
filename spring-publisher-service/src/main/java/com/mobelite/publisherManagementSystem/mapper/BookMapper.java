package com.mobelite.publisherManagementSystem.mapper;

import com.mobelite.publisherManagementSystem.dto.request.book.BookCreateRequestDto;
import com.mobelite.publisherManagementSystem.dto.request.book.BookUpdateRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Book;
import org.mapstruct.*;

/**
 * MapStruct mapper for Book entity and DTOs.
 * Handles mapping between entity and various DTO representations.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    /**
     * Maps BookCreateRequest to Book entity.
     * @param request The create request
     * @return The Book entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    Book toEntity(BookCreateRequestDto request);

    /**
     * Maps Book entity to BookResponse.
     * @param book The Book entity
     * @return The BookResponse
     */
    @Mapping(target = "author", source = "author")
    BookResponseDto toResponse(Book book);

    /**
     * Maps Book entity to BookSummaryResponse.
     * @param book The Book entity
     * @return The BookSummaryResponse
     */
    @Mapping(target = "authorName", source = "author.name")
    BookSummaryResponseDto toSummaryResponse(Book book);


    /**
     * Updates existing Book entity from BookUpdateRequest.
     * Only updates non-null fields from the request.
     * @param request The update request
     * @param book The existing Book entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(BookUpdateRequestDto request, @MappingTarget Book book);
}