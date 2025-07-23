package com.mobelite.publisherManagementSystem.controller;

// Jakarta/Javax imports
import com.mobelite.publisherManagementSystem.dto.response.book.BookResponseDto;
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

// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

// Application-specific imports
import com.mobelite.publisherManagementSystem.dto.request.author.AuthorRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.ApiResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.author.AuthorResponseDto;
import com.mobelite.publisherManagementSystem.service.AuthorService;

import java.util.List;

/**
 * REST Controller for Author operations.
 * Provides endpoints for CRUD operations and search functionality.
 */
@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Author Management", description = "APIs for managing authors")
public class AuthorController {

    private final AuthorService authorService;

    @Operation(summary = "Create a new author", description = "Creates a new author with the provided information")
    @PostMapping
    public ResponseEntity<ApiResponseDto<AuthorResponseDto>> createAuthor(
            @Valid @RequestBody AuthorRequestDto authorRequestDto) {

        AuthorResponseDto createdAuthor = authorService.createAuthor(authorRequestDto);

        ApiResponseDto<AuthorResponseDto> response = ApiResponseDto.<AuthorResponseDto>builder()
                .success(true)
                .message("Author created successfully")
                .data(createdAuthor)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get author by ID", description = "Retrieves an author by their unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<AuthorResponseDto>> getAuthorById(
            @Parameter(description = "Author ID") @PathVariable Long id) {

        AuthorResponseDto author = authorService.getAuthorById(id);

        ApiResponseDto<AuthorResponseDto> response = ApiResponseDto.<AuthorResponseDto>builder()
                .success(true)
                .message("Author retrieved successfully")
                .data(author)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all authors", description = "Retrieves all authors without pagination")
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<AuthorResponseDto>>> getAllAuthors(
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        Page<AuthorResponseDto> response = authorService.getAllAuthors(pageable);

        return  ResponseEntity.ok(ApiResponseDto.success(response));
    }
}
