package com.mobelite.publisherManagementSystem.mapper;

import com.mobelite.publisherManagementSystem.dto.request.magazine.MagazineRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Magazine;
import org.mapstruct.*;

/**
 * MapStruct mapper for Magazine entity and DTOs.
 * Handles conversion between entity and DTO objects with custom mapping logic.
 */
@Mapper(
        componentModel = "spring",
        uses = {AuthorMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MagazineMapper {

    /**
     * Convert Magazine entity to full response DTO.
     */
    @Mapping(target = "authors", source = "authors")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "issueNumber", source = "issueNumber")
    MagazineResponseDto toResponseDto(Magazine magazine);

    /**
     * Convert Magazine entity to summary DTO.
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "issueNumber", source = "issueNumber")
    MagazineSummaryResponseDto toSummaryDto(Magazine magazine);

    /**
     * Convert request DTO to Magazine entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "issueNumber", source = "issueNumber")
    @Mapping(target = "publicationDate", source = "publicationDate")
    Magazine toEntity(MagazineRequestDto requestDto);

    /**
     * Update existing Magazine entity from request DTO.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "issueNumber", source = "issueNumber")
    @Mapping(target = "publicationDate", source = "publicationDate")
    void updateEntityFromDto(MagazineRequestDto requestDto, @MappingTarget Magazine magazine);
}