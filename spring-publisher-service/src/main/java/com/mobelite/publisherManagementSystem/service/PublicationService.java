package com.mobelite.publisherManagementSystem.service;

import com.mobelite.publisherManagementSystem.dto.response.publication.GroupedPublicationsResponse;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Service interface for Publication operations.
 * Defines business logic methods for publication management.
 */
public interface PublicationService {

    /**
     * Get a publication by ID.
     * @param id The publication ID
     * @return The publication response
     */
    PublicationResponseDto getPublicationById(Long id);

    /**
     * Get all publications with pagination.
     * @param pageable Pagination information
     * @return Page of publication summaries
     */
    Page<PublicationSummaryResponseDto> getAllPublications(Pageable pageable);

    /**
     * Retrieves all publications grouped by type (books and magazines)
     * @return GroupedPublicationsResponse containing separate lists for books and magazines
     */
    GroupedPublicationsResponse getAllPublicationsGroupedByType();

    /**
     * Search publications by title.
     * @param title The title to search for
     * @param pageable Pagination information
     * @return Page of publication summaries
     */
    Page<PublicationSummaryResponseDto> searchPublicationsByTitle(String title, Pageable pageable);

    /**
     * Delete a publication by ID.
     * @param id The publication ID
     */
    void deletePublication(Long id);

    /**
     * Check if a publication exists by ID.
     * @param id The publication ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Check if a publication exists by title.
     * @param title The title to check
     * @return true if exists, false otherwise
     */
    boolean existsByTitle(String title);


}
