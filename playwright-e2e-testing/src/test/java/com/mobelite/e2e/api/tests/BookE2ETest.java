package com.mobelite.e2e.api.tests;

import com.microsoft.playwright.APIRequestContext;
import com.mobelite.e2e.api.core.ApiAssertions;
import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.endpoints.AuthorEndpoints;
import com.mobelite.e2e.api.endpoints.BookEndPoints;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.fixtures.BookFixtures;
import com.mobelite.e2e.api.models.*;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import com.mobelite.e2e.api.models.request.BookRequest;
import com.mobelite.e2e.extensions.ApiContextExtension;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;

import static com.mobelite.e2e.shared.constants.HttpStatusCodes.*;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Book Management")
@Feature("Book API")
@Story("E2E Testing")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Book API E2E Tests")
@Slf4j
@ExtendWith(ApiContextExtension.class)

public class BookE2ETest {

    private ApiClient apiClient;
    private BookEndPoints bookEndpoints;
    private AuthorEndpoints authorEndpoints;
    private AuthorFixtures authorFixtures;


    // Test data holders
    private Author testAuthor;
    private Book createdBook;


    @BeforeAll
    void initAll(APIRequestContext apiRequestContext) {
        log.info("Initializing AuthorE2ETest suite");
        apiClient = new ApiClient(apiRequestContext);
        authorEndpoints = new AuthorEndpoints(apiClient);
        authorFixtures = new AuthorFixtures(apiClient);
        bookEndpoints = new BookEndPoints(apiClient);

        // Create a shared author for all tests
        testAuthor = authorFixtures.createAuthorAndRegisterForCleanup(
                authorFixtures.createValidAuthorRequest()
        );
        log.info("Shared author created with ID: {}", testAuthor.getId());
    }


    @AfterEach
    void tearDown() {
        // Clean up created book
        if (createdBook != null) {
            try {
                bookEndpoints.deleteBook(createdBook.getId());
                log.info("Cleaned up book with ID: {}", createdBook.getId());
            } catch (Exception e) {
                log.warn("Failed to clean up book with ID: {}", createdBook.getId());
            }
        }

        // Clean up test author
        if (testAuthor != null) {
            try {
                authorEndpoints.deleteAuthor(testAuthor.getId());
                log.info("Cleaned up author with ID: {}", testAuthor.getId());
            } catch (Exception e) {
                log.warn("Failed to clean up author with ID: {}", testAuthor.getId());
            }
        }
    }

    // ======================= POSITIVE TEST CASES ======================= //

    @Test
    @Story("Book Creation")
    @Description("Should successfully create a new book with valid data")
    void shouldCreateBookWithValidData() {
        // Given
        BookRequest bookRequest = BookFixtures.createValidBookRequest(testAuthor.getId());

        // When
        createdBook = bookEndpoints.createBookAndValidateStructure(bookRequest);

        // Then
        assertThat(createdBook).isNotNull();
        assertThat(createdBook.getId()).isNotNull().isPositive();
        assertThat(createdBook.getTitle()).isEqualTo(bookRequest.getTitle());
        assertThat(createdBook.getPublicationDate()).isEqualTo(bookRequest.getPublicationDate());
        assertThat(createdBook.getIsbn()).isEqualTo(bookRequest.getIsbn());
        assertThat(createdBook.getAuthor()).isNotNull();
        assertThat(createdBook.getAuthor().getId()).isEqualTo(testAuthor.getId());

        log.info("Successfully created book with ID: {}", createdBook.getId());
    }

    @Test
    @Order(2)
    @Story("Book Retrieval")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should successfully retrieve a book by ID")
    void shouldRetrieveBookById() {
        // Given
        BookRequest bookRequest = BookFixtures.createValidBookRequest(testAuthor.getId());
        createdBook = bookEndpoints.createBookAndValidateStructure(bookRequest);

        // When
        Book retrievedBook = bookEndpoints.getBookByIdAndValidateStructure(createdBook.getId());

        // Then
        assertThat(retrievedBook).isNotNull();
        assertThat(retrievedBook.getId()).isEqualTo(createdBook.getId());
        assertThat(retrievedBook.getTitle()).isEqualTo(createdBook.getTitle());
        assertThat(retrievedBook.getPublicationDate()).isEqualTo(createdBook.getPublicationDate());
        assertThat(retrievedBook.getIsbn()).isEqualTo(createdBook.getIsbn());
        assertThat(retrievedBook.getAuthor()).isNotNull();
        assertThat(retrievedBook.getAuthor().getId()).isEqualTo(testAuthor.getId());

        log.info("Successfully retrieved book with ID: {}", retrievedBook.getId());
    }

    @Test
    @Order(3)
    @Story("Book Listing")
    @Severity(SeverityLevel.NORMAL)
    @Description("Should successfully retrieve all books")
    void shouldRetrieveAllBooks() {
        // Given
        BookRequest bookRequest = BookFixtures.createValidBookRequest(testAuthor.getId());
        createdBook = bookEndpoints.createBookAndValidateStructure(bookRequest);

        // When
        PageResponse<Book> pageResponse = bookEndpoints.getAllBooksAndValidateStructure();

        // Then
        assertThat(pageResponse).isNotNull();
        assertThat(pageResponse.getContent()).isNotNull();
        assertThat(pageResponse.getTotalElements()).isGreaterThan(0);
        assertThat(pageResponse.getContent()).anyMatch(book -> book.getId().equals(createdBook.getId()));

        log.info("Successfully retrieved {} books", pageResponse.getTotalElements());
    }





    // ======================= EDGE CASES & SPECIAL SCENARIOS ======================= //

    @Test
    @Order(30)
    @Story("Book Creation")
    @Severity(SeverityLevel.MINOR)
    @Description("Should create book with minimum valid data")
    void shouldCreateBookWithMinimumValidData() {
        // Given
        BookRequest minimalRequest = BookRequest.builder()
                .title("A")  // Minimum length title
                .publicationDate(LocalDate.of(1900, 1, 1))  // Very old date
                .isbn("1234567890")  // Minimum ISBN-10
                .authorId(testAuthor.getId())
                .build();

        // When
        createdBook = bookEndpoints.createBookAndValidateStructure(minimalRequest);

        // Then
        assertThat(createdBook).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo(minimalRequest.getTitle());
        assertThat(createdBook.getPublicationDate()).isEqualTo(minimalRequest.getPublicationDate());
        assertThat(createdBook.getIsbn()).isEqualTo(minimalRequest.getIsbn());

        log.info("Successfully created book with minimal data");
    }
}
