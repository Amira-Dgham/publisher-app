package com.mobelite.publisher.ui.factory;

import com.mobelite.publisher.ui.models.AuthorRequest;
import io.qameta.allure.Step;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static com.mobelite.publisher.ui.utils.generateDataUtils.generateRandomBirthDate;

public class AuthorFactory {
    private static final AtomicLong authorCounter = new AtomicLong(1);
    private static final Faker faker = new Faker();

    @Step("Create author request")
    public static AuthorRequest createAuthorRequest(String name, LocalDate birthDate, String nationality) {
        return AuthorRequest.builder()
                .name(name)
                .birthDate(birthDate)
                .nationality(nationality)
                .build();
    }

    @Step("Create valid author request with Faker")
    public static AuthorRequest createValidAuthorRequest() {
        String name = faker.name().firstName() + "_" + authorCounter.getAndIncrement();
        LocalDate birthDate = generateRandomBirthDate(20, 70);
        String nationality = faker.country().name();
        return createAuthorRequest(name, birthDate, nationality);
    }
}
