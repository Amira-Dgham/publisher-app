package com.mobelite.publisherManagementSystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.mobelite.publisherManagementSystem.dto.request.book.BookCreateRequestDto;
import com.mobelite.publisherManagementSystem.dto.request.book.BookUpdateRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.ApiResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import com.mobelite.publisherManagementSystem.service.BookService;

/**
 * REST Controller for Book entity operations.
 * Provides endpoints for CRUD operations and search functionality.
 */
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Book Management", description = "API for managing books in the library system")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Create a new book", description = "Creates a new book in the library system")
    public ResponseEntity<ApiResponseDto<BookResponseDto>> createBook(
            @Valid @RequestBody BookCreateRequestDto request) {

        BookResponseDto createdBook = bookService.createBook(request);

        ApiResponseDto<BookResponseDto> response = ApiResponseDto.<BookResponseDto>builder()
                .success(true)
                .message("Book created successfully")
                .data(createdBook)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Updates an existing book by ID")
    public ResponseEntity<ApiResponseDto<BookResponseDto>> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequestDto request) {

        BookResponseDto updatedBook = bookService.updateBook(id, request);

        ApiResponseDto<BookResponseDto> response = ApiResponseDto.<BookResponseDto>builder()
                .success(true)
                .message("Book updated successfully")
                .data(updatedBook)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieves a book by its unique identifier")
    public ResponseEntity<ApiResponseDto<BookResponseDto>> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) {

        BookResponseDto book = bookService.getBookById(id);

        ApiResponseDto<BookResponseDto> response = ApiResponseDto.<BookResponseDto>builder()
                .success(true)
                .message("Book retrieved successfully")
                .data(book)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "Get book by ISBN", description = "Retrieves a book by its ISBN")
    public ResponseEntity<ApiResponseDto<BookResponseDto>> getBookByIsbn(
            @Parameter(description = "Book ISBN") @PathVariable String isbn) {

        BookResponseDto book = bookService.getBookByIsbn(isbn);

        ApiResponseDto<BookResponseDto> response = ApiResponseDto.<BookResponseDto>builder()
                .success(true)
                .message("Book retrieved successfully")
                .data(book)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieves all books with pagination")
    public ResponseEntity<ApiResponseDto<Page<BookResponseDto>>> getAllBooks(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {

        Page<BookResponseDto> books = bookService.getAllBooks(pageable);

        ApiResponseDto<Page<BookResponseDto>> response = ApiResponseDto.<Page<BookResponseDto>>builder()
                .success(true)
                .message("Books retrieved successfully")
                .data(books)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get books by author", description = "Retrieves books by author ID")
    public ResponseEntity<ApiResponseDto<Page<BookSummaryResponseDto>>> getBooksByAuthor(
            @Parameter(description = "Author ID") @PathVariable Long authorId,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {

        Page<BookSummaryResponseDto> books = bookService.getBooksByAuthor(authorId, pageable);

        ApiResponseDto<Page<BookSummaryResponseDto>> response = ApiResponseDto.<Page<BookSummaryResponseDto>>builder()
                .success(true)
                .message("Books by author retrieved successfully")
                .data(books)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Deletes a book by its ID")
    public ResponseEntity<ApiResponseDto<Void>> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {

        bookService.deleteBook(id);

        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .success(true)
                .message("Book deleted successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if book exists", description = "Checks if a book exists by its ID")
    public ResponseEntity<ApiResponseDto<Boolean>> existsById(
            @Parameter(description = "Book ID") @PathVariable Long id) {

        boolean exists = bookService.existsById(id);

        ApiResponseDto<Boolean> response = ApiResponseDto.<Boolean>builder()
                .success(true)
                .message("Existence check completed")
                .data(exists)
                .build();

        return ResponseEntity.ok(response);
    }
}