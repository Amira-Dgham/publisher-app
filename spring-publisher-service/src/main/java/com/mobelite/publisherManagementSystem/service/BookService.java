package com.mobelite.publisherManagementSystem.service;

import com.mobelite.publisherManagementSystem.dto.request.book.BookCreateRequestDto;
import com.mobelite.publisherManagementSystem.dto.request.book.BookUpdateRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Book entity operations.
 * Defines business logic methods for book management.
 */
public interface BookService {

    /**
     * Create a new book.
     * @param request The book creation request
     * @return The created book response
     * @throws IllegalArgumentException if author doesn't exist or ISBN already exists
     */
    BookResponseDto createBook(BookCreateRequestDto request);

    /**
     * Update an existing book.
     * @param id The book ID to update
     * @param request The book update request
     * @return The updated book response
     * @throws IllegalArgumentException if book doesn't exist or ISBN conflict
     */
    BookResponseDto updateBook(Long id, BookUpdateRequestDto request);

    /**
     * Get a book by ID.
     * @param id The book ID
     * @return The book response
     * @throws IllegalArgumentException if book doesn't exist
     */
    BookResponseDto getBookById(Long id);

    /**
     * Get a book by ISBN.
     * @param isbn The ISBN
     * @return The book response
     * @throws IllegalArgumentException if book doesn't exist
     */
    BookResponseDto getBookByIsbn(String isbn);

    /**
     * Get all books with pagination.
     * @param pageable Pagination information
     * @return Page of book summaries
     */
    Page<BookSummaryResponseDto> getAllBooks(Pageable pageable);

    /**
     * Get books by author ID.
     * @param authorId The author ID
     * @param pageable Pagination information
     * @return Page of book summaries
     */
    Page<BookSummaryResponseDto> getBooksByAuthor(Long authorId, Pageable pageable);


    /**
     * Delete a book by ID.
     * @param id The book ID to delete
     * @throws IllegalArgumentException if book doesn't exist
     */
    void deleteBook(Long id);

    /**
     * Check if a book exists by ID.
     * @param id The book ID
     * @return true if book exists, false otherwise
     */
    boolean existsById(Long id);

}