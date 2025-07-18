package com.mobelite.publisherManagementSystem.service;

import com.mobelite.publisherManagementSystem.dto.request.magazine.MagazineRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Service interface for Magazine operations.
 * Defines business logic methods for magazine management.
 */
public interface MagazineService {

    /**
     * Create a new magazine.
     * @param requestDto Magazine creation request
     * @return Created magazine response
     */
    MagazineResponseDto createMagazine(MagazineRequestDto requestDto);

    /**
     * Update an existing magazine.
     * @param id Magazine ID
     * @param requestDto Magazine update request
     * @return Updated magazine response
     */
    MagazineResponseDto updateMagazine(Long id, MagazineRequestDto requestDto);

    /**
     * Get magazine by ID.
     * @param id Magazine ID
     * @return Magazine response
     */
    MagazineResponseDto getMagazineById(Long id);


    /**
     * Get all magazines with pagination.
     *
     * @param pageable Pagination information
     * @return Page of magazine summaries
     */
    Page<MagazineSummaryResponseDto> getAllMagazines(Pageable pageable);



    /**
     * Delete magazine by ID.
     * @param id Magazine ID
     */
    void deleteMagazine(Long id);

}