package com.mobelite.publisher.api.factory;

import com.mobelite.publisher.api.models.Author;
import com.mobelite.publisher.api.models.request.AuthorRequest;
import io.qameta.allure.Step;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static com.mobelite.publisher.api.utils.GenerateDataUtils.generateRandomBirthDate;


public class AuthorFactory {
    private final AtomicLong authorCounter = new AtomicLong(1);
    private static final String TEST_AUTHOR_PREFIX = "TEST_AUTHOR_";
    private static final Faker faker = new Faker();

    @Step("Create author request")
    public AuthorRequest createAuthorRequest(String name, LocalDate birthDate, String nationality) {
        return AuthorRequest.builder()
                .name(name)
                .birthDate(birthDate)
                .nationality(nationality)
                .build();
    }

    @Step("Create valid author request with Faker")
    public AuthorRequest createValidAuthorRequest() {
        String name = faker.name().firstName() + "_" + authorCounter.getAndIncrement();
        LocalDate birthDate = generateRandomBirthDate(20, 70);
        String nationality = faker.country().name();
        return createAuthorRequest(name, birthDate, nationality);
    }

    @Step("Create minimal author request with Faker")
    public AuthorRequest createMinimalAuthorRequest() {
        String name = TEST_AUTHOR_PREFIX + faker.name().firstName() + "_" + authorCounter.getAndIncrement();
        return createAuthorRequest(name, null, null);
    }

    @Step("Create invalid author request with Faker")
    public AuthorRequest createInvalidAuthorRequest() {
        String name = "";
        LocalDate birthDate = LocalDate.now().plusDays(1);
        String nationality = faker.lorem().characters(60);
        return createAuthorRequest(name, birthDate, nationality);
    }

    @Step("Create duplicate author request from existing Author")
    public AuthorRequest createDuplicateFromAuthor(Author existing) {
        return createAuthorRequest(
                existing.getName(),
                existing.getBirthDate(),
                existing.getNationality()
        );
    }
}
