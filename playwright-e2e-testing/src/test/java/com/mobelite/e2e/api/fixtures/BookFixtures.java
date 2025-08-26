package com.mobelite.e2e.api.fixtures;

import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.Book;
import com.mobelite.e2e.api.models.request.BookRequest;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Fixture class for generating test data for Book entities and requests.
 * Provides various factory methods for creating valid and invalid book data.
 */
public class BookFixtures {

    private static final Faker faker = new Faker();

    // ---------------------- VALID BOOK REQUESTS ---------------------- //

    /**
     * Creates a valid BookRequest with random data and specified author ID.
     */
    public static BookRequest createValidBookRequest(Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(generateRandomPublicationDate())
                .isbn(generateValidISBN())
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a valid BookRequest with custom title and specified author ID.
     */
    public static BookRequest createValidBookRequestWithTitle(String title, Long authorId) {
        return BookRequest.builder()
                .title(title)
                .publicationDate(generateRandomPublicationDate())
                .isbn(generateValidISBN())
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a valid BookRequest with custom publication date and specified author ID.
     */
    public static BookRequest createValidBookRequestWithDate(LocalDate publicationDate, Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(publicationDate)
                .isbn(generateValidISBN())
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a valid BookRequest with custom ISBN and specified author ID.
     */
    public static BookRequest createValidBookRequestWithISBN(String isbn, Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(generateRandomPublicationDate())
                .isbn(isbn)
                .authorId(authorId)
                .build();
    }

    /**
     * Creates multiple valid BookRequests with the same author ID.
     */
    public static List<BookRequest> createMultipleValidBookRequests(Long authorId, int count) {
        return faker.collection(() -> createValidBookRequest(authorId))
                .len(count)
                .generate();
    }

    // ---------------------- INVALID BOOK REQUESTS ---------------------- //

    /**
     * Creates a BookRequest with null title.
     */
    public static BookRequest createBookRequestWithNullTitle(Long authorId) {
        return BookRequest.builder()
                .title(null)
                .publicationDate(generateRandomPublicationDate())
                .isbn(generateValidISBN())
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a BookRequest with empty title.
     */
    public static BookRequest createBookRequestWithEmptyTitle(Long authorId) {
        return BookRequest.builder()
                .title("")
                .publicationDate(generateRandomPublicationDate())
                .isbn(generateValidISBN())
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a BookRequest with title that's too long.
     */
    public static BookRequest createBookRequestWithTooLongTitle(Long authorId) {
        return BookRequest.builder()
                .title(faker.lorem().characters(256, 300)) // Exceeds typical 255 char limit
                .publicationDate(generateRandomPublicationDate())
                .isbn(generateValidISBN())
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a BookRequest with null publication date.
     */
    public static BookRequest createBookRequestWithNullPublicationDate(Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(null)
                .isbn(generateValidISBN())
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a BookRequest with future publication date.
     */
    public static BookRequest createBookRequestWithFutureDate(Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(LocalDate.now().plusYears(1))
                .isbn(generateValidISBN())
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a BookRequest with null ISBN.
     */
    public static BookRequest createBookRequestWithNullISBN(Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(generateRandomPublicationDate())
                .isbn(null)
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a BookRequest with empty ISBN.
     */
    public static BookRequest createBookRequestWithEmptyISBN(Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(generateRandomPublicationDate())
                .isbn("")
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a BookRequest with invalid ISBN format.
     */
    public static BookRequest createBookRequestWithInvalidISBN(Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(generateRandomPublicationDate())
                .isbn("invalid-isbn-123")
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a BookRequest with null author ID.
     */
    public static BookRequest createBookRequestWithNullAuthorId() {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(generateRandomPublicationDate())
                .isbn(generateValidISBN())
                .authorId(null)
                .build();
    }

    /**
     * Creates a BookRequest with non-existent author ID.
     */
    public static BookRequest createBookRequestWithNonExistentAuthorId() {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(generateRandomPublicationDate())
                .isbn(generateValidISBN())
                .authorId(999999L) // Assume this ID doesn't exist
                .build();
    }

    // ---------------------- BOOK ENTITIES ---------------------- //

    /**
     * Creates a complete Book entity with Author for testing responses.
     */
    public static Book createCompleteBook(Long bookId, Author author) {
        return Book.builder()
                .id(bookId)
                .title(faker.book().title())
                .publicationDate(generateRandomPublicationDate())
                .isbn(generateValidISBN())
                .author(author)
                .build();
    }

    /**
     * Creates a Book entity from a BookRequest and Author.
     */
    public static Book createBookFromRequest(Long bookId, BookRequest request, Author author) {
        return Book.builder()
                .id(bookId)
                .title(request.getTitle())
                .publicationDate(request.getPublicationDate())
                .isbn(request.getIsbn())
                .author(author)
                .build();
    }

    // ---------------------- HELPER METHODS ---------------------- //

    /**
     * Generates a random publication date between 1950 and current year.
     */
    private static LocalDate generateRandomPublicationDate() {
        LocalDate start = LocalDate.of(1950, 1, 1);
        LocalDate end = LocalDate.now();

        long startEpochDay = start.toEpochDay();
        long endEpochDay = end.toEpochDay();

        long randomDay = startEpochDay + faker.number().numberBetween(0, endEpochDay - startEpochDay + 1);

        return LocalDate.ofEpochDay(randomDay);
    }

    /**
     * Generates a valid ISBN-13 format.
     */
    private static String generateValidISBN() {
        return "978-" + faker.number().digits(1) + "-" +
                faker.number().digits(3) + "-" +
                faker.number().digits(5) + "-" +
                faker.number().digits(1);
    }

    /**
     * Generates a valid ISBN-10 format.
     */
    public static String generateValidISBN10() {
        return faker.number().digits(1) + "-" +
                faker.number().digits(3) + "-" +
                faker.number().digits(5) + "-" +
                faker.regexify("[0-9X]");
    }

    // ---------------------- SPECIFIC TEST SCENARIOS ---------------------- //

    /**
     * Creates a BookRequest for classic literature testing.
     */
    public static BookRequest createClassicBookRequest(Long authorId) {
        String[] classicTitles = {
                "Pride and Prejudice", "To Kill a Mockingbird", "1984",
                "The Great Gatsby", "Jane Eyre", "Wuthering Heights"
        };

        return BookRequest.builder()
                .title(faker.options().option(classicTitles))
                .publicationDate(LocalDate.of(faker.number().numberBetween(1800, 1950),
                        faker.number().numberBetween(1, 12),
                        faker.number().numberBetween(1, 28)))
                .isbn(generateValidISBN())
                .authorId(authorId)
                .build();
    }

    /**
     * Creates a BookRequest for modern literature testing.
     */
    public static BookRequest createModernBookRequest(Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title())
                .publicationDate(LocalDate.of(faker.number().numberBetween(2000, LocalDate.now().getYear()),
                        faker.number().numberBetween(1, 12),
                        faker.number().numberBetween(1, 28)))
                .isbn(generateValidISBN())
                .authorId(authorId)
                .build();
    }


}