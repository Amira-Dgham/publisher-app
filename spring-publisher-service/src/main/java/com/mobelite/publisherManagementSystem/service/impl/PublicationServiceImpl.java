package com.mobelite.publisherManagementSystem.service.impl;

import com.mobelite.publisherManagementSystem.dto.response.publication.GroupedPublicationsResponse;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Book;
import com.mobelite.publisherManagementSystem.entity.Magazine;
import com.mobelite.publisherManagementSystem.entity.Publication;
import com.mobelite.publisherManagementSystem.exception.ResourceNotFoundException;
import com.mobelite.publisherManagementSystem.mapper.PublicationMapper;
import com.mobelite.publisherManagementSystem.repository.PublicationRepository;
import com.mobelite.publisherManagementSystem.service.PublicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service implementation for Publication operations.
 * Implements business logic for publication management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PublicationServiceImpl implements PublicationService {
    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;


    @Override
    @Transactional(readOnly = true)
    public PublicationResponseDto getPublicationById(Long id) {

        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with ID: " + id));
        return publicationMapper.toResponseDto(publication);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PublicationSummaryResponseDto> getAllPublications(Pageable pageable) {

        Page<Publication> publications = publicationRepository.findAll(pageable);
        return publications.map(publicationMapper::toSummaryResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupedPublicationsResponse getAllPublicationsGroupedByType() {
        List<Book> books = publicationRepository.findAllBooks(); // Custom repository method
        List<Magazine> magazines = publicationRepository.findAllMagazines(); // Custom repository method

        return GroupedPublicationsResponse.builder()
                .books(books.stream().map(publicationMapper::bookToSummaryDto).toList())
                .magazines(magazines.stream().map(publicationMapper::magazineToSummaryDto).toList())
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public Page<PublicationSummaryResponseDto> searchPublicationsByTitle(String title, Pageable pageable) {

        Page<Publication> publications = publicationRepository.findByTitleContainingIgnoreCase(title, pageable);
        return publications.map(publicationMapper::toSummaryResponseDto);
    }

    @Override
    public void deletePublication(Long id) {

        if (!publicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Publication not found with ID: " + id);
        }
        publicationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return publicationRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTitle(String title) {
        return publicationRepository.existsByTitle(title);
    }

}