package com.mobelite.publisherManagementSystem.mapper;

import com.mobelite.publisherManagementSystem.dto.request.magazine.MagazineRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Magazine;
import org.mapstruct.*;


@Mapper(
        componentModel = "spring",
        uses = {AuthorMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MagazineMapper {


    @Mapping(target = "authors", source = "authors")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "issueNumber", source = "issueNumber")
    MagazineResponseDto toResponseDto(Magazine magazine);


    @Mapping(target = "title", source = "title")
    @Mapping(target = "issueNumber", source = "issueNumber")
    @Mapping(target = "authors", source = "authors")
    MagazineSummaryResponseDto toSummaryDto(Magazine magazine);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "issueNumber", source = "issueNumber")
    @Mapping(target = "publicationDate", source = "publicationDate")
    Magazine toEntity(MagazineRequestDto requestDto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "issueNumber", source = "issueNumber")
    @Mapping(target = "publicationDate", source = "publicationDate")
    void updateEntityFromDto(MagazineRequestDto requestDto, @MappingTarget Magazine magazine);
}