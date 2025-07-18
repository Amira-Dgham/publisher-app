package com.mobelite.publisherManagementSystem.service.impl;

import com.mobelite.publisherManagementSystem.dto.request.magazine.MagazineRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Author;
import com.mobelite.publisherManagementSystem.entity.Magazine;
import com.mobelite.publisherManagementSystem.exception.ResourceNotFoundException;
import com.mobelite.publisherManagementSystem.mapper.MagazineMapper;
import com.mobelite.publisherManagementSystem.repository.AuthorRepository;
import com.mobelite.publisherManagementSystem.repository.MagazineRepository;
import com.mobelite.publisherManagementSystem.service.MagazineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Implementation of MagazineService interface.
 * Provides business logic for magazine operations with proper error handling and logging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MagazineServiceImpl implements MagazineService {
    private final MagazineMapper magazineMapper;
    private final MagazineRepository magazineRepository;
    private final AuthorRepository authorRepository;

    @Override
    public MagazineResponseDto createMagazine(MagazineRequestDto requestDto) {

        List<Author> authors = authorRepository.findAllById(requestDto.getAuthorIds());
        if (authors.size() != requestDto.getAuthorIds().size()) {
            throw new ResourceNotFoundException("One or more authors not found");
        }

        // Map and create magazine
        Magazine magazine = magazineMapper.toEntity(requestDto);
        magazine.setAuthors(authors);
        Magazine savedMagazine = magazineRepository.save(magazine);
        return magazineMapper.toResponseDto(savedMagazine);
    }

    @Override
    public MagazineResponseDto updateMagazine(Long id, MagazineRequestDto requestDto) {

        Magazine existingMagazine = magazineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Magazine not found with ID: " + id));

        // Validate all authors exist
        List<Author> authors = authorRepository.findAllById(requestDto.getAuthorIds());
        if (authors.size() != requestDto.getAuthorIds().size()) {
            throw new ResourceNotFoundException("One or more authors not found");
        }

        // Update magazine fields
        magazineMapper.updateEntityFromDto(requestDto, existingMagazine);
        existingMagazine.setAuthors(authors);
        Magazine updatedMagazine = magazineRepository.save(existingMagazine);
        return magazineMapper.toResponseDto(updatedMagazine);
    }

    @Override
    @Transactional(readOnly = true)
    public MagazineResponseDto getMagazineById(Long id) {

        Magazine magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Magazine not found with ID: " + id));
        return magazineMapper.toResponseDto(magazine);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<MagazineSummaryResponseDto> getAllMagazines(Pageable pageable) {
        Page<Magazine> magazines = magazineRepository.findAll(pageable);
        return magazines.map(magazineMapper::toSummaryDto);
    }


    @Override
    public void deleteMagazine(Long id) {

        if (!magazineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Magazine not found with ID: " + id);
        }
        magazineRepository.deleteById(id);
    }
}
