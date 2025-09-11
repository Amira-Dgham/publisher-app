package com.mobelite.e2e.api.tests;

import com.mobelite.e2e.api.core.ApiAssertions;
import com.mobelite.e2e.api.endpoints.AuthorApiEndPoint;
import com.mobelite.e2e.api.endpoints.BookApiEndPoint;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.fixtures.BookFixtures;
import com.mobelite.e2e.api.models.*;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import com.mobelite.e2e.api.models.request.BookRequest;
import com.mobelite.e2e.api.models.response.ApiResponse;
import com.mobelite.e2e.api.models.response.PageResponse;
import com.mobelite.e2e.config.BaseTest;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import static com.mobelite.e2e.shared.constants.ApiEndpoints.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Book Management")
@Feature("Book API")
@Story("E2E Testing")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Book API E2E Tests")
@Slf4j
public class BookE2ETest extends BaseTest {

    private final BookApiEndPoint bookApi = new BookApiEndPoint();
    private final AuthorApiEndPoint authorApi = new AuthorApiEndPoint();
    private final BookFixtures bookFixtures = new BookFixtures();
    private final AuthorFixtures authorFixtures = new AuthorFixtures();
    private Author sharedAuthor;

    @BeforeAll
    void setUpAll() {
        bookApi.init(api);
        authorApi.init(api);

        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        sharedAuthor = authorApi.createAuthor(request);
    }

    @AfterEach
    void tearDownEach() {
        bookApi.cleanUpEach(BOOKS_BY_ID);
    }

    @AfterAll
    void cleanupShared() {
        try {
            authorApi.deleteAuthor(sharedAuthor.getId());
            log.info("Cleaned up SHARED AUTHOR {}", sharedAuthor.getId());
        } catch (Exception e) {
            log.warn("Failed to delete SHARED AUTHOR {}: {}", sharedAuthor.getId(), e.getMessage());
        }
    }

    @Test
    @DisplayName("Create book with valid data")
    void createBookWithValidData() {
        BookRequest request = bookFixtures.createValidBookRequest(sharedAuthor.getId());
        Book created = bookApi.createBook(request);

        assertNotNull(created.getId());
        assertEquals(request.getTitle(), created.getTitle());
        assertEquals(request.getPublicationDate(), created.getPublicationDate());
        assertEquals(request.getIsbn(), created.getIsbn());
        assertEquals(sharedAuthor.getId(), created.getAuthor().getId());
    }

    @Test
    @DisplayName("Create book with minimal data")
    void createBookWithMinimalData() {
        BookRequest request = bookFixtures.createMinimalBookRequest(sharedAuthor.getId());
        Book created = bookApi.createBook(request);

        assertNotNull(created.getId());
        assertEquals(request.getTitle(), created.getTitle());
    }

    @Test
    @DisplayName("Retrieve book by ID")
    void getBookById() {
        BookRequest request = bookFixtures.createValidBookRequest(sharedAuthor.getId());
        Book created = bookApi.createBook(request);

        Book retrieved = bookApi.getBookById(created.getId());
        assertEquals(created.getId(), retrieved.getId());
    }

    @Test
    @DisplayName("Retrieve books with pagination")
    void getAllBooks() {
        BookRequest request = bookFixtures.createValidBookRequest(sharedAuthor.getId());
        Book created = bookApi.createBook(request);

        PageResponse<Book> page = bookApi.getAllBooks();
        assertNotNull(page);
        assertTrue(page.hasContent());
    }

    @Test
    @DisplayName("Delete book successfully")
    void deleteBook() {
        BookRequest request = bookFixtures.createValidBookRequest(sharedAuthor.getId());
        Book created = bookApi.createBook(request,false);

        ApiResponse<Void> response = bookApi.deleteBook(created.getId());
        ApiAssertions.assertSuccess(response);
    }

    @Test
    @DisplayName("Fail to create book with invalid ISBN")
    void createBookWithInvalidISBN() {
        BookRequest invalid = bookFixtures.createBookRequestWithInvalidISBN(sharedAuthor.getId());
        ApiResponse<?> response = bookApi.createInvalidBook(invalid, 400);

        assertFalse(response.isSuccess());
        log.info("Book creation failed as expected due to invalid ISBN");
    }

    @Test
    @DisplayName("Fail to delete non-existent book")
    void deleteNonExistentBook() {
        Long nonExistentId = 999999L;
        ApiResponse<?> response = bookApi.deleteNonExistentBook(nonExistentId, 404);

        assertFalse(response.isSuccess());
        log.info("Delete non-existent book test passed as expected");
    }
}