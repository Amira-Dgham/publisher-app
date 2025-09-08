package com.mobelite.e2e.api.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.core.ApiAssertions;
import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.core.ApiRequestBuilder;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.fixtures.BookFixtures;
import com.mobelite.e2e.api.models.*;
import com.mobelite.e2e.api.models.request.BookRequest;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import com.mobelite.e2e.api.core.BaseApiEndPoint;
import com.mobelite.e2e.shared.constants.HttpMethod;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static com.mobelite.e2e.shared.constants.ApiEndpoints.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Book Management")
@Feature("Book API")
@Story("E2E Testing")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Book API E2E Tests")
@Slf4j
public class BookE2ETest extends BaseApiEndPoint<Book, BookRequest> {

    private final BookFixtures bookFixtures = new BookFixtures();
    private final AuthorFixtures authorFixtures = new AuthorFixtures();

    private Author sharedAuthor; // shared author for all books

    private final List<Long> booksToCleanup = new ArrayList<>();

    // ---- Implement abstract methods ----
    @Override protected String getEntityName() { return "Book"; }
    @Override protected String getBaseEndpoint() { return BOOKS_BASE; }
    @Override protected String getItemByIdEndpoint() { return BOOKS_BY_ID; }
    @Override protected String getItemSchema() { return "/schemas/book-schema.json"; }
    @Override protected BookRequest createSharedEntityRequest() { return null; } // skip shared book
    @Override protected TypeReference<ApiResponse<Book>> getItemTypeReference() { return new TypeReference<>() {}; }
    @Override protected TypeReference<ApiResponse<PageResponse<Book>>> getPageTypeReference() { return new TypeReference<>() {}; }

    @Override
    protected boolean shouldCreateSharedEntity() { return false; } // no shared book

    @BeforeAll
    void setupSharedAuthor() {
        // Ensure apiClient is initialized
        if (this.apiClient == null) {
            this.apiClient = new ApiClient(api);
        }

        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        ApiResponse<Author> response =executeRequest(
                new ApiRequestBuilder(apiClient, HttpMethod.POST, AUTHORS_BASE).body(request),
                201,
                new TypeReference<ApiResponse<Author>>() {}
        );

        sharedAuthor = response.getData();
        log.info("Shared author created for books with ID: {}", sharedAuthor.getId());
    }

    @AfterAll
    void cleanupSharedAuthor() {
        if (sharedAuthor != null && sharedAuthor.getId() != null) {
            try {
                ApiResponse<Void> response = executeRequest(
                        new ApiRequestBuilder(apiClient, HttpMethod.DELETE, buildPath(AUTHOR_BY_ID, sharedAuthor.getId())),
                        200,
                        new TypeReference<ApiResponse<Void>>() {}
                );
                ApiAssertions.assertMessageContains(response, "deleted successfully");
                log.info("Shared author deleted: {}", sharedAuthor.getId());
            } catch (Exception e) {
                log.warn("Failed to delete shared author {}: {}", sharedAuthor.getId(), e.getMessage());
            }
        }
    }

    // -------- CREATE TESTS -------- //

    @Test
    @DisplayName("Create book with valid data")
    void createBookWithValidData() {
        BookRequest request = bookFixtures.createValidBookRequest(sharedAuthor.getId());
        Book created = createAndValidate(request, getBaseEndpoint());
        trackForCleanup(created.getId());

        assertNotNull(created.getId());
        assertEquals(request.getTitle(), created.getTitle());
        assertEquals(request.getPublicationDate(), created.getPublicationDate());
        assertEquals(request.getIsbn(), created.getIsbn());
        assertEquals(sharedAuthor.getId(), created.getAuthor().getId());

        log.info("Book created successfully: {}", created.getId());
    }

    @Test
    @DisplayName("Create book with minimal data")
    void createBookWithMinimalData() {
        BookRequest request = bookFixtures.createMinimalBookRequest(sharedAuthor.getId());
        Book created = createAndValidate(request, getBaseEndpoint());
        trackForCleanup(created.getId());

        assertNotNull(created.getId());
        assertEquals(request.getTitle(), created.getTitle());

        log.info("Minimal book created successfully: {}", created.getId());
    }

    // -------- READ TESTS -------- //

    @Test
    @DisplayName("Retrieve book by ID")
    void getBookById() {
        BookRequest request = bookFixtures.createValidBookRequest(sharedAuthor.getId());
        Book created = createAndValidate(request, getBaseEndpoint());
        trackForCleanup(created.getId());

        Book retrieved = getByIdAndValidate(created.getId(), getItemByIdEndpoint());
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getTitle(), retrieved.getTitle());
    }

    @Test
    @DisplayName("Retrieve books with pagination")
    void getAllBooks() {
        BookRequest request = bookFixtures.createValidBookRequest(sharedAuthor.getId());
        Book created = createAndValidate(request, getBaseEndpoint());
        trackForCleanup(created.getId());

        PageResponse<Book> page = getAllAndValidate(getBaseEndpoint());
        assertNotNull(page);
        assertTrue(page.hasContent());
    }

    // -------- DELETE TESTS -------- //

    @Test
    @DisplayName("Delete book successfully")
    void deleteBook() {
        BookRequest request = bookFixtures.createValidBookRequest(sharedAuthor.getId());
        Book created = createAndValidate(request, getBaseEndpoint());

        ApiResponse<Void> response = deleteAndValidate(created.getId(), getItemByIdEndpoint());
        ApiAssertions.assertSuccess(response);
    }

    @Test
    @DisplayName("Fail to delete non-existent book")
    void deleteNonExistentBook() {
        Long nonExistentId = 999999L;
        ApiResponse<?> response = executeErrorRequest(delete(buildPath(getItemByIdEndpoint(), nonExistentId)), 404);
        assertFalse(response.isSuccess());
    }

    // -------- NEGATIVE TESTS -------- //

    @Test
    @DisplayName("Fail to create book with invalid ISBN")
    void createBookWithInvalidISBN() {
        BookRequest invalid = bookFixtures.createBookRequestWithInvalidISBN(sharedAuthor.getId());
        ApiResponse<?> response = executeErrorRequest(post(getBaseEndpoint()).body(invalid), 400);

        assertFalse(response.isSuccess());
        log.info("Invalid book creation failed as expected");
    }
}