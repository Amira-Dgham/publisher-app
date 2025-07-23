package com.mobelite.publisherManagementSystem.service;

import com.mobelite.publisherManagementSystem.dto.request.author.AuthorRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.author.AuthorResponseDto;
import com.mobelite.publisherManagementSystem.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Author business logic operations.
 * Defines the contract for author-related business operations.
 */
public interface AuthorService {

    /**
     * Create a new author.
     *
     * @param authorRequestDto Author creation request
     * @return Created author response
     */
    AuthorResponseDto createAuthor(AuthorRequestDto authorRequestDto);


    /**
     * Get author by ID.
     *
     * @param id Author ID
     * @return Author response
     */
    AuthorResponseDto getAuthorById(Long id);

    Page<AuthorResponseDto> getAllAuthors(Pageable pageable);

    /**
     * Check if an author exists by ID.
     *
     * @param id Author ID
     * @return True if author exists, false otherwise
     */
    boolean existsById(Long id);
}