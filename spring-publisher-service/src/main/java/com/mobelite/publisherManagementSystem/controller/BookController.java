package com.mobelite.publisherManagementSystem.controller;

// Jakarta/Javax imports
import jakarta.validation.Valid;

// Lombok imports
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Spring Framework imports
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Swagger/OpenAPI documentation imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

// Application-specific DTO imports
import com.mobelite.publisherManagementSystem.dto.request.book.BookCreateRequestDto;
import com.mobelite.publisherManagementSystem.dto.request.book.BookUpdateRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.ApiResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;

// Application service imports
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

    /**
     * Create a new book.
     *
     * @param request The book creation request
     * @return The created book response
     */
    @PostMapping
    @Operation(summary = "Create a new book", description = "Creates a new book in the library system")

    public ResponseEntity<ApiResponseDto<BookResponseDto>> createBook(@Valid @RequestBody BookCreateRequestDto request) {
        BookResponseDto response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(response,"Book created successfully"));
    }

    /**
     * Update an existing book.
     *
     * @param id      The book ID
     * @param request The book update request
     * @return The updated book response
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Updates an existing book by ID")
    public ResponseEntity<ApiResponseDto<BookResponseDto>> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequestDto request) {
        BookResponseDto response = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponseDto.success( response,"Book updated successfully"));
    }

    /**
     * Get a book by ID.
     * @param id The book ID
     * @return The book response
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieves a book by its ID")
    public ResponseEntity<BookResponseDto> getBookById(@Parameter(description = "Book ID") @PathVariable Long id) {
        BookResponseDto response = bookService.getBookById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a book by ISBN.
     *
     * @param isbn The book ISBN
     * @return The book response
     */
    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "Get book by ISBN", description = "Retrieves a book by its ISBN")
    public ResponseEntity<ApiResponseDto<BookResponseDto>> getBookByIsbn(@Parameter(description = "Book ISBN") @PathVariable String isbn) {
        BookResponseDto response = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    /**
     * Get all books with pagination.
     *
     * @param pageable Pagination information
     * @return Page of book summaries
     */
    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieves all books with pagination")
    public ResponseEntity<ApiResponseDto<Page<BookResponseDto>>> getAllBooks(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<BookResponseDto> response = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    /**
     * Get books by author ID.
     *
     * @param authorId The author ID
     * @param pageable Pagination information
     * @return Page of book summaries
     */
    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get books by author", description = "Retrieves books by author ID")
    public ResponseEntity<ApiResponseDto<Page<BookSummaryResponseDto>>> getBooksByAuthor(
            @Parameter(description = "Author ID") @PathVariable Long authorId,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<BookSummaryResponseDto> response = bookService.getBooksByAuthor(authorId, pageable);
        return ResponseEntity.ok(ApiResponseDto.success(response));
    }


    /**
     * Delete a book by ID.
     * @param id The book ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Deletes a book by its ID")
    public ResponseEntity<Void> deleteBook(@Parameter(description = "Book ID") @PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if a book exists by ID.
     *
     * @param id The book ID
     * @return Boolean response
     */
    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if book exists", description = "Checks if a book exists by its ID")
    public ResponseEntity<ApiResponseDto<Boolean>> existsById(@Parameter(description = "Book ID") @PathVariable Long id) {
        boolean exists = bookService.existsById(id);
        return ResponseEntity.ok(ApiResponseDto.success(exists));
    }


}