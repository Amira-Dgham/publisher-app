package com.mobelite.publisherManagementSystem.service.impl;

import com.mobelite.publisherManagementSystem.dto.request.book.BookCreateRequestDto;
import com.mobelite.publisherManagementSystem.dto.request.book.BookUpdateRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Author;
import com.mobelite.publisherManagementSystem.entity.Book;
import com.mobelite.publisherManagementSystem.exception.DuplicateResourceException;
import com.mobelite.publisherManagementSystem.exception.ResourceNotFoundException;
import com.mobelite.publisherManagementSystem.mapper.BookMapper;
import com.mobelite.publisherManagementSystem.repository.AuthorRepository;
import com.mobelite.publisherManagementSystem.repository.BookRepository;
import com.mobelite.publisherManagementSystem.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of BookService interface.
 * Handles all business logic for book operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public BookResponseDto createBook(BookCreateRequestDto request) {

        try {
            // Validate author exists
            Author author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author with ID " + request.getAuthorId() + " not found"));

            // Map request to entity
            Book book = bookMapper.toEntity(request);
            book.setAuthor(author);

            // Save and return response
            Book savedBook = bookRepository.save(book);

            return bookMapper.toResponse(savedBook);

        } catch (DataIntegrityViolationException ex) {
            // Handle all database constraint violations
            if (ex.getMessage().contains("isbn") || ex.getMessage().contains("ISBN")) {
                throw new DuplicateResourceException("Book with ISBN " + request.getIsbn() + " already exists");
            }
            throw new IllegalArgumentException("Database constraint violation: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public BookResponseDto updateBook(Long id, BookUpdateRequestDto request) {

        // Find existing book
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found"));

        // Validate ISBN uniqueness if ISBN is being updated
        if (request.getIsbn() != null && !request.getIsbn().equals(existingBook.getIsbn())) {
            if (bookRepository.existsByIsbnAndIdNot(request.getIsbn(), id)) {
                throw new ResourceNotFoundException("Book with ISBN " + request.getIsbn() + " already exists");
            }
        }

        // Validate author exists if author is being updated
        if (request.getAuthorId() != null) {
            Author author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author with ID " + request.getAuthorId() + " not found"));
            existingBook.setAuthor(author);
        }

        // Update entity with mapper
        bookMapper.updateEntityFromRequest(request, existingBook);

        // Save and return response
        Book updatedBook = bookRepository.save(existingBook);

        return bookMapper.toResponse(updatedBook);
    }

    @Override
    public BookResponseDto getBookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found"));

        return bookMapper.toResponse(book);
    }

    @Override
    public BookResponseDto getBookByIsbn(String isbn) {

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ISBN " + isbn + " not found"));

        return bookMapper.toResponse(book);
    }

    @Override
    public Page<BookResponseDto> getAllBooks(Pageable pageable) {

        return bookRepository.findAll(pageable)
                .map(bookMapper::toResponse);
    }

    @Override
    public Page<BookSummaryResponseDto> getBooksByAuthor(Long authorId, Pageable pageable) {

        return bookRepository.findByAuthorId(authorId, pageable)
                .map(bookMapper::toSummaryResponse);
    }


    @Override
    @Transactional
    public void deleteBook(Long id) {

        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book with ID " + id + " not found");
        }

        bookRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return bookRepository.existsById(id);
    }

}