package com.mobelite.e2e.api.fixtures;

import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static com.mobelite.e2e.shared.helpers.GenerateData.generateRandomBirthDate;


@Slf4j
public class AuthorFixtures {

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
        return createAuthorRequest(name, null, null); // minimal author
    }

    @Step("Create invalid author request with Faker")
    public AuthorRequest createInvalidAuthorRequest() {
        String name = ""; // invalid
        LocalDate birthDate = LocalDate.now().plusDays(1); // future date
        String nationality = faker.lorem().characters(60); // too long
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