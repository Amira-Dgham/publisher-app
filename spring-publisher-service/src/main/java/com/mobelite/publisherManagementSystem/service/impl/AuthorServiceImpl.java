package com.mobelite.publisherManagementSystem.service.impl;

import com.mobelite.publisherManagementSystem.dto.request.author.AuthorRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.author.AuthorResponseDto;
import com.mobelite.publisherManagementSystem.entity.Author;
import com.mobelite.publisherManagementSystem.exception.DuplicateResourceException;
import com.mobelite.publisherManagementSystem.exception.ResourceNotFoundException;
import com.mobelite.publisherManagementSystem.mapper.AuthorMapper;
import com.mobelite.publisherManagementSystem.repository.AuthorRepository;
import com.mobelite.publisherManagementSystem.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AuthorService interface.
 * Handles all business logic related to author operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    public AuthorResponseDto createAuthor(AuthorRequestDto authorRequestDto) {

        // Check for duplicate author name
        if (authorRepository.existsByName(authorRequestDto.getName())) {
            throw new DuplicateResourceException("Author with name '" + authorRequestDto.getName() + "' already exists");
        }

        Author author = authorMapper.toEntity(authorRequestDto);
        Author savedAuthor = authorRepository.save(author);

        return authorMapper.toResponseDto(savedAuthor);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponseDto getAuthorById(Long id) {
        Author author = authorRepository.findByIdWithPublications(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with ID: " + id));

        return authorMapper.toResponseDto(author);
    }

    /**
     * Get all authors with their books and magazines with pagination support.
     */
    @Transactional(readOnly = true)
    @Override
    public Page<AuthorResponseDto> getAllAuthors(Pageable pageable) {
        try {
            // Use pageable in repository call - you'll need to update your repository method
            Page<Author> authorPage = authorRepository.findAllWithMagazines(pageable);

            // Initialize books collection for each author in the current page
            for (Author author : authorPage.getContent()) {
                author.getBooks().size();
            }

            // Map authors to response DTOs
            List<AuthorResponseDto> authorResponseDtos = authorPage.getContent().stream()
                    .map(author -> {
                        try {
                            return authorMapper.toResponseDto(author);
                        } catch (Exception e) {
                            log.error("Error mapping author with ID {}: {}", author.getId(), e.getMessage(), e);
                            throw new RuntimeException("Failed to map author data for ID: " + author.getId(), e);
                        }
                    })
                    .collect(Collectors.toList());

            // Return a new Page with mapped content
            return new PageImpl<>(authorResponseDtos, pageable, authorPage.getTotalElements());

        } catch (Exception e) {
            log.error("Error fetching authors: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch authors", e);
        }
    }

    @Override
    public void deleteAuthor(Long id) {
        log.info("Attempting to delete author with ID: {}", id);

        // Check if author exists before attempting to delete
        if (!authorRepository.existsById(id)) {
            log.warn("Author with ID {} not found for deletion", id);
            throw new ResourceNotFoundException("Author not found with ID: " + id);
        }

        try {
            // Delete the author
            authorRepository.deleteById(id);
            log.info("Successfully deleted author with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting author with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete author with ID: " + id, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return authorRepository.existsById(id);
    }
}