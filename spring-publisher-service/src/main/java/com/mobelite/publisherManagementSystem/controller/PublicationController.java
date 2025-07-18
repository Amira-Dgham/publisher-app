package com.mobelite.publisherManagementSystem.controller;

// Jakarta/Javax imports
import com.mobelite.publisherManagementSystem.dto.response.publication.GroupedPublicationsResponse;

// Lombok imports
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Spring Framework imports
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

// Application DTO imports

import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationSummaryResponseDto;

// Application service imports
import com.mobelite.publisherManagementSystem.service.PublicationService;

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

    /**
     * Get a publication by ID.
     * @param id The publication ID
     * @return The publication response
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get publication by ID", description = "Retrieves a publication by its ID")
    public ResponseEntity<PublicationResponseDto> getPublicationById(@Parameter(description = "Publication ID") @PathVariable Long id) {
        PublicationResponseDto response = publicationService.getPublicationById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all publications with pagination.
     * @param pageable Pagination information
     * @return Page of publication summaries
     */
    @GetMapping
    @Operation(summary = "Get all publications", description = "Retrieves all publications with pagination")
    public ResponseEntity<Page<PublicationSummaryResponseDto>> getAllPublications(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<PublicationSummaryResponseDto> response = publicationService.getAllPublications(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all publications", description = "Retrieves all publications grouped by book or magazine")
    @GetMapping("/grouped")
    public ResponseEntity<GroupedPublicationsResponse> getAllPublicationsGroupedByType() {
        return ResponseEntity.ok(publicationService.getAllPublicationsGroupedByType());
    }

    /**
     * Search publications by title.
     * @param title The title to search for
     * @param pageable Pagination information
     * @return Page of publication summaries
     */
    @GetMapping("/search/title")
    @Operation(summary = "Search publications by title", description = "Searches publications by title (case-insensitive)")
    public ResponseEntity<Page<PublicationSummaryResponseDto>> searchPublicationsByTitle(
            @Parameter(description = "Title to search for") @RequestParam String title,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<PublicationSummaryResponseDto> response = publicationService.searchPublicationsByTitle(title, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a publication by ID.
     * @param id The publication ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a publication", description = "Deletes a publication by its ID")
    public ResponseEntity<Void> deletePublication(@Parameter(description = "Publication ID") @PathVariable Long id) {
        publicationService.deletePublication(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if a publication exists by ID.
     * @param id The publication ID
     * @return Boolean response
     */
    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if publication exists", description = "Checks if a publication exists by its ID")
    public ResponseEntity<Boolean> existsById(@Parameter(description = "Publication ID") @PathVariable Long id) {
        boolean exists = publicationService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    /**
     * Check if a publication exists by title.
     * @param title The publication title
     * @return Boolean response
     */
    @GetMapping("/title/{title}/exists")
    @Operation(summary = "Check if publication exists by title", description = "Checks if a publication exists by its title")
    public ResponseEntity<Boolean> existsByTitle(@Parameter(description = "Publication title") @PathVariable String title) {
        boolean exists = publicationService.existsByTitle(title);
        return ResponseEntity.ok(exists);
    }


}
