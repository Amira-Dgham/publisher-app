package com.mobelite.e2e.api.fixtures;

import com.mobelite.e2e.api.models.request.BookRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class BookFixtures {

    private static final Faker faker = new Faker();
    private static final AtomicLong counter = new AtomicLong(1);
    private static final String TEST_BOOK_PREFIX = "TEST_BOOK_";

    // ---------------------- FACTORY METHODS ---------------------- //

    private static LocalDate randomPublicationDate() {
        int year = ThreadLocalRandom.current().nextInt(1900, LocalDate.now().getYear() + 1);
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int day = ThreadLocalRandom.current().nextInt(1, Month.of(month).length(false) + 1);
        return LocalDate.of(year, month, day);
    }

    @Step("Create valid book request")
    public static BookRequest createValidBookRequest(Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title() + "_" + counter.getAndIncrement())
                .publicationDate(randomPublicationDate())
                .isbn(faker.number().digits(13)) // valid ISBN-like number
                .authorId(authorId)
                .build();
    }

    @Step("Create minimal book request")
    public static BookRequest createMinimalBookRequest(Long authorId) {
        return BookRequest.builder()
                .title(TEST_BOOK_PREFIX + counter.getAndIncrement())
                .publicationDate(LocalDate.of(1900, 1, 1))
                .isbn("1234567890") // simple 10-digit
                .authorId(authorId)
                .build();
    }

    @Step("Create book request with invalid ISBN")
    public static BookRequest createBookRequestWithInvalidISBN(Long authorId) {
        return BookRequest.builder()
                .title("InvalidISBN_" + counter.getAndIncrement())
                .publicationDate(randomPublicationDate())
                .isbn("INVALID") // deliberately invalid
                .authorId(authorId)
                .build();
    }

    @Step("Create book request with empty title")
    public static BookRequest createBookRequestWithEmptyTitle(Long authorId) {
        return BookRequest.builder()
                .title("") // invalid
                .publicationDate(randomPublicationDate())
                .isbn(faker.number().digits(13))
                .authorId(authorId)
                .build();
    }
}