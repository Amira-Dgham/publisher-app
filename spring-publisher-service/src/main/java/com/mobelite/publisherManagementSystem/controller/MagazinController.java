package com.mobelite.publisherManagementSystem.controller;

import com.mobelite.publisherManagementSystem.dto.request.magazine.MagazineRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.ApiResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import com.mobelite.publisherManagementSystem.service.MagazineService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Magazine Management operations.
 * Provides endpoints for creating, reading, updating, and deleting magazines.
 */
@RestController
@RequestMapping("/api/v1/magazines")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Magazine Management", description = "APIs for managing magazines")
public class MagazinController {

    private final MagazineService magazineService;

    @PostMapping
    @Operation(
            summary = "Create a new magazine",
            description = "Creates a new magazine with the provided details and associates it with existing authors"
    )
    public ResponseEntity<ApiResponseDto<MagazineResponseDto>> createMagazine(
            @Valid @RequestBody MagazineRequestDto requestDto) {
        MagazineResponseDto responseDto = magazineService.createMagazine(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(responseDto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing magazine",
            description = "Updates an existing magazine with the provided details"
    )
    public ResponseEntity<ApiResponseDto<MagazineResponseDto>> updateMagazine(
            @Parameter(description = "Magazine ID", required = true) @PathVariable Long id,
            @Valid @RequestBody MagazineRequestDto requestDto) {
        MagazineResponseDto responseDto = magazineService.updateMagazine(id, requestDto);
        return ResponseEntity.ok(ApiResponseDto.success(responseDto));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get magazine by ID",
            description = "Retrieves a magazine by its unique identifier"
    )
    public ResponseEntity<ApiResponseDto<MagazineResponseDto>> getMagazineById(
            @Parameter(description = "Magazine ID", required = true) @PathVariable Long id) {
        MagazineResponseDto responseDto = magazineService.getMagazineById(id);
        return ResponseEntity.ok(ApiResponseDto.success(responseDto));
    }

    @GetMapping
    @Operation(
            summary = "Get all magazines",
            description = "Retrieves all magazines with pagination and sorting support"
    )
    public ResponseEntity<ApiResponseDto<Page<MagazineSummaryResponseDto>>> getAllMagazines(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field", example = "title") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)", example = "ASC") @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<MagazineSummaryResponseDto> magazines = magazineService.getAllMagazines(pageable);
        return ResponseEntity.ok(ApiResponseDto.success(magazines));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete magazine",
            description = "Deletes a magazine by its unique identifier"
    )
    public ResponseEntity<Void> deleteMagazine(
            @Parameter(description = "Magazine ID", required = true) @PathVariable Long id) {
        magazineService.deleteMagazine(id);
        return ResponseEntity.noContent().build();
    }
}