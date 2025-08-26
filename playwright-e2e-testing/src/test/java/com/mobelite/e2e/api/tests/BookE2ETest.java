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


    }

    @AfterAll
    void cleanupAuthorsAfterAllTests() {
        if (authorFixtures.getCleanupCount() > 0) {
            log.info("Cleaning up {} authors after all tests", authorFixtures.getCleanupCount());
            authorFixtures.cleanupAllAuthors();
        } else {
            log.info("No authors to clean up after all tests");
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
    @Description("Should successfully retrieve all books")
    void shouldRetrieveAllBooks() {
        // Given
        log.info("amira id ID: {}", testAuthor.getId());

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







    @Test
    @Order(27)
    @Story("Book Deletion")
    @Description("Should return 404 when deleting non-existent book")
    void shouldReturn404WhenDeletingNonExistentBook() {
        // Given
        Long nonExistentId = 999999L;



        // When & Then
        ApiResponse<?> response = bookEndpoints.deleteNonExistentBookAndValidateError(nonExistentId);

        log.info("Successfully returned 404 when deleting non-existent book ID: {}", nonExistentId);
    }



    @Test
    @Order(31)
    @Story("Book Creation")
    @Severity(SeverityLevel.MINOR)
    @Description("Should create book with current date")
    void shouldCreateBookWithCurrentDate() {
        // Given
        BookRequest currentDateRequest = BookFixtures.createValidBookRequestWithDate(
                LocalDate.now(), testAuthor.getId());

        // When
        createdBook = bookEndpoints.createBookAndValidateStructure(currentDateRequest);

        // Then
        assertThat(createdBook).isNotNull();
        assertThat(createdBook.getPublicationDate()).isEqualTo(LocalDate.now());

        log.info("Successfully created book with current date");
    }

    @Test
    @Order(32)
    @Story("Book Creation")
    @Severity(SeverityLevel.MINOR)
    @Description("Should create multiple books for same author")
    void shouldCreateMultipleBooksForSameAuthor() {
        // Given
        List<BookRequest> bookRequests = BookFixtures.createMultipleValidBookRequests(testAuthor.getId(), 3);

        // When
        List<Book> createdBooks = bookRequests.stream()
                .map(bookEndpoints::createBookAndValidateStructure)
                .toList();

        // Then
        assertThat(createdBooks).hasSize(3);
        assertThat(createdBooks).allMatch(book -> book.getAuthor().getId().equals(testAuthor.getId()));
        assertThat(createdBooks).extracting(Book::getId).doesNotHaveDuplicates();

        // Verify through API
        PageResponse<Book> authorBooks = bookEndpoints.getBooksByAuthorIdAndValidateStructure(testAuthor.getId());
        assertThat(authorBooks.getTotalElements()).isGreaterThanOrEqualTo(3);

        // Clean up
        createdBooks.forEach(book -> {
            try {
                bookEndpoints.deleteBook(book.getId());
            } catch (Exception e) {
                log.warn("Failed to clean up book with ID: {}", book.getId());
            }
        });

        log.info("Successfully created {} books for same author", createdBooks.size());
    }

    @Test
    @Order(34)
    @Story("Book Validation")
    @Severity(SeverityLevel.MINOR)
    @Description("Should handle special characters in book title")
    void shouldHandleSpecialCharactersInTitle() {
        // Given
        String specialTitle = "Book Title: A Story of Love & War (2023) - Part I";
        BookRequest specialRequest = BookFixtures.createValidBookRequestWithTitle(
                specialTitle, testAuthor.getId());

        // When
        createdBook = bookEndpoints.createBookAndValidateStructure(specialRequest);

        // Then
        assertThat(createdBook).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo(specialTitle);

        log.info("Successfully created book with special characters in title");
    }

    @Test
    @Order(35)
    @Story("Book Validation")
    @Severity(SeverityLevel.MINOR)
    @Description("Should handle ISBN-10 and ISBN-13 formats")
    void shouldHandleDifferentISBNFormats() {
        // Test ISBN-13
        BookRequest isbn13Request = BookFixtures.createValidBookRequestWithISBN(
                "978-0-123-45678-9", testAuthor.getId());
        Book isbn13Book = bookEndpoints.createBookAndValidateStructure(isbn13Request);

        assertThat(isbn13Book).isNotNull();
        assertThat(isbn13Book.getIsbn()).isEqualTo("978-0-123-45678-9");
        bookEndpoints.deleteBook(isbn13Book.getId());

        // Test ISBN-10
        BookRequest isbn10Request = BookFixtures.createValidBookRequestWithISBN(
                BookFixtures.generateValidISBN10(), testAuthor.getId());
        createdBook = bookEndpoints.createBookAndValidateStructure(isbn10Request);

        assertThat(createdBook).isNotNull();
        assertThat(createdBook.getIsbn()).isNotNull();

        log.info("Successfully handled both ISBN-10 and ISBN-13 formats");
    }

    // ======================= PERFORMANCE & LOAD TESTS ======================= //

    @Test
    @Order(40)
    @Story("Performance")
    @Severity(SeverityLevel.MINOR)
    @Description("Should handle bulk book operations efficiently")
    void shouldHandleBulkBookOperationsEfficiently() {
        // Given
        int bookCount = 10;
        List<BookRequest> bulkRequests = BookFixtures.createMultipleValidBookRequests(
                testAuthor.getId(), bookCount);

        // When - Bulk creation
        long startTime = System.currentTimeMillis();
        List<Book> bulkBooks = bulkRequests.stream()
                .map(bookEndpoints::createBookAndValidateStructure)
                .toList();
        long creationTime = System.currentTimeMillis() - startTime;

        // Then
        assertThat(bulkBooks).hasSize(bookCount);
        assertThat(creationTime).isLessThan(10000); // Should complete within 10 seconds

        // Verify retrieval performance
        startTime = System.currentTimeMillis();
        PageResponse<Book> allBooks = bookEndpoints.getAllBooksAndValidateStructure();
        long retrievalTime = System.currentTimeMillis() - startTime;

        assertThat(allBooks.getTotalElements()).isGreaterThanOrEqualTo(bookCount);
        assertThat(retrievalTime).isLessThan(5000); // Should complete within 5 seconds

        // Clean up
        bulkBooks.forEach(book -> {
            try {
                bookEndpoints.deleteBook(book.getId());
            } catch (Exception e) {
                log.warn("Failed to clean up book with ID: {}", book.getId());
            }
        });

        log.info("Successfully handled bulk operations: {} books created in {}ms, retrieved in {}ms",
                bookCount, creationTime, retrievalTime);
    }


@Test
@Order(5)
@Story("Book Filtering")
@Severity(SeverityLevel.NORMAL)
@Description("Should successfully retrieve books by author ID")
void shouldRetrieveBooksByAuthorId() {
    // Given
    BookRequest bookRequest = BookFixtures.createValidBookRequest(testAuthor.getId());
    createdBook = bookEndpoints.createBookAndValidateStructure(bookRequest);

    // When
    PageResponse<Book> pageResponse = bookEndpoints.getBooksByAuthorIdAndValidateStructure(testAuthor.getId());

    // Then
    assertThat(pageResponse).isNotNull();
    assertThat(pageResponse.getContent()).isNotNull();
    assertThat(pageResponse.getTotalElements()).isGreaterThan(0);
    assertThat(pageResponse.getContent())
            .allMatch(book -> book.getAuthor().getId().equals(testAuthor.getId()));

    log.info("Successfully retrieved {} books for author ID: {}",
            pageResponse.getTotalElements(), testAuthor.getId());
}

@Test
@Order(6)
@Story("Book Update")
@Severity(SeverityLevel.CRITICAL)
@Description("Should successfully update a book")
void shouldUpdateBook() {
    // Given
    BookRequest initialRequest = BookFixtures.createValidBookRequest(testAuthor.getId());
    createdBook = bookEndpoints.createBookAndValidateStructure(initialRequest);

    BookRequest updateRequest = BookFixtures.createValidBookRequestWithTitle(
            "Updated Book Title", testAuthor.getId());

    // When
    Book updatedBook = bookEndpoints.updateBookAndValidateStructure(createdBook.getId(), updateRequest);

    // Then
    assertThat(updatedBook).isNotNull();
    assertThat(updatedBook.getId()).isEqualTo(createdBook.getId());
    assertThat(updatedBook.getTitle()).isEqualTo(updateRequest.getTitle());
    assertThat(updatedBook.getPublicationDate()).isEqualTo(updateRequest.getPublicationDate());
    assertThat(updatedBook.getIsbn()).isEqualTo(updateRequest.getIsbn());

    createdBook = updatedBook; // Update reference for cleanup
    log.info("Successfully updated book with ID: {}", updatedBook.getId());
}

@Test
@Order(7)
@Story("Book Deletion")
@Severity(SeverityLevel.CRITICAL)
@Description("Should successfully delete a book")
void shouldDeleteBook() {
    // Given
    BookRequest bookRequest = BookFixtures.createValidBookRequest(testAuthor.getId());
    createdBook = bookEndpoints.createBookAndValidateStructure(bookRequest);
    Long bookId = createdBook.getId();

    // When
    ApiResponse<Void> deleteResponse = bookEndpoints.deleteBookAndValidateStructure(bookId);

    // Then
    ApiAssertions.assertMessageContains(deleteResponse, "deleted successfully");

    // Verify book is deleted
    ApiResponse<?> getResponse = bookEndpoints.getNonExistentBookAndValidateError(bookId);


    createdBook = null; // Reset for cleanup
    log.info("Successfully deleted book with ID: {}", bookId);
}

}
