package com.mobelite.publisher.api.factory;

import com.mobelite.publisher.api.models.request.BookRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static com.mobelite.publisher.api.utils.GenerateDataUtils.generateRandomDateInYear;


@Slf4j
public class BookFactory {

    private static final Faker faker = new Faker();
    private static final AtomicLong counter = new AtomicLong(1);
    private static final String TEST_BOOK_PREFIX = "TEST_BOOK_";

    @Step("Create valid book request")
    public static BookRequest createValidBook(Long authorId) {
        return BookRequest.builder()
                .title(faker.book().title() + "_" + counter.getAndIncrement())
                .publicationDate(generateRandomDateInYear(2000))
                .isbn(faker.number().digits(13)) // valid ISBN-like number
                .authorId(authorId)
                .build();
    }

    @Step("Create minimal book request")
    public static BookRequest createMinimalBook(Long authorId) {
        return BookRequest.builder()
                .title(TEST_BOOK_PREFIX + counter.getAndIncrement())
                .publicationDate(LocalDate.of(1900, 1, 1))
                .isbn("1234567890") // simple 10-digit
                .authorId(authorId)
                .build();
    }

    @Step("Create book request with invalid ISBN")
    public static BookRequest createWithInvalidISBN(Long authorId) {
        return BookRequest.builder()
                .title("InvalidISBN_" + counter.getAndIncrement())
                .publicationDate(generateRandomDateInYear(1900))
                .isbn("INVALID") // deliberately invalid
                .authorId(authorId)
                .build();
    }

    @Step("Create book request with empty title")
    public static BookRequest createWithEmptyTitle(Long authorId) {
        return BookRequest.builder()
                .title("") // invalid
                .publicationDate(generateRandomDateInYear(1900))
                .isbn(faker.number().digits(13))
                .authorId(authorId)
                .build();
    }
}