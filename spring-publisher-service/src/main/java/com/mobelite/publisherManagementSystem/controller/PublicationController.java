package com.mobelite.publisherManagementSystem.controller;

import com.mobelite.publisherManagementSystem.dto.response.ApiResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.GroupedPublicationsResponse;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationSummaryResponseDto;
import com.mobelite.publisherManagementSystem.service.PublicationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Publication entity operations.
 * Provides endpoints for CRUD operations, search functionality, and statistics.
 */
@RestController
@RequestMapping("/api/v1/publications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Publication Management", description = "API for managing publications in the library system")
public class PublicationController {

    private final PublicationService publicationService;

    @GetMapping("/{id}")
    @Operation(summary = "Get publication by ID", description = "Retrieves a publication by its ID")
    public ResponseEntity<ApiResponseDto<PublicationResponseDto>> getPublicationById(
            @Parameter(description = "Publication ID") @PathVariable Long id) {
        PublicationResponseDto response = publicationService.getPublicationById(id);
        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all publications", description = "Retrieves all publications with pagination")
    public ResponseEntity<ApiResponseDto<Page<PublicationSummaryResponseDto>>> getAllPublications(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<PublicationSummaryResponseDto> response = publicationService.getAllPublications(pageable);
        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    @GetMapping("/grouped")
    @Operation(summary = "Get grouped publications", description = "Retrieves all publications grouped by type (book or magazine)")
    public ResponseEntity<ApiResponseDto<GroupedPublicationsResponse>> getAllPublicationsGroupedByType() {
        GroupedPublicationsResponse response = publicationService.getAllPublicationsGroupedByType();
        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    @GetMapping("/search/title")
    @Operation(summary = "Search publications by title", description = "Searches publications by title (case-insensitive)")
    public ResponseEntity<ApiResponseDto<Page<PublicationSummaryResponseDto>>> searchPublicationsByTitle(
            @Parameter(description = "Title to search for") @RequestParam String title,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<PublicationSummaryResponseDto> response = publicationService.searchPublicationsByTitle(title, pageable);
        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a publication", description = "Deletes a publication by its ID")
    public ResponseEntity<Void> deletePublication(
            @Parameter(description = "Publication ID") @PathVariable Long id) {
        publicationService.deletePublication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if publication exists", description = "Checks if a publication exists by its ID")
    public ResponseEntity<ApiResponseDto<Boolean>> existsById(
            @Parameter(description = "Publication ID") @PathVariable Long id) {
        boolean exists = publicationService.existsById(id);
        return ResponseEntity.ok(ApiResponseDto.success(exists));
    }

    @GetMapping("/title/{title}/exists")
    @Operation(summary = "Check if publication exists by title", description = "Checks if a publication exists by its title")
    public ResponseEntity<ApiResponseDto<Boolean>> existsByTitle(
            @Parameter(description = "Publication title") @PathVariable String title) {
        boolean exists = publicationService.existsByTitle(title);
        return ResponseEntity.ok(ApiResponseDto.success(exists));
    }
}