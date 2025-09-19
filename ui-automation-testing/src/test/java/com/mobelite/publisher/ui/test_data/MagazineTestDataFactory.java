package com.mobelite.publisher.ui.test_data;

import com.mobelite.models.request.MagazineRequest;
import io.qameta.allure.Step;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MagazineTestDataFactory {
    private static final AtomicLong magazineCounter = new AtomicLong(1);
    private static final Faker faker = new Faker();

    @Step("Create magazine request with parameters")
    public static MagazineRequest createMagazineRequest(String title, int issueNumber, LocalDate publicationDate, List<Long> authorIds) {
        return MagazineRequest.builder()
                .title(title)
                .issueNumber(issueNumber)
                .publicationDate(publicationDate.toString())
                .authorIds(authorIds != null ? authorIds : Collections.emptyList())
                .build();
    }

    @Step("Create valid magazine request with Faker and dynamic authors")
    public static MagazineRequest createValidMagazineRequest(List<Long> authorIds) {
        String title = "Magazine_" + faker.book().title() + "_" + magazineCounter.getAndIncrement();
        int issueNumber = faker.number().numberBetween(1, 500);
        LocalDate publicationDate = LocalDate.now().minusDays(faker.number().numberBetween(0, 3650));
        return createMagazineRequest(title, issueNumber, publicationDate, authorIds);
    }

    @Step("Create static magazine request with dynamic authors")
    public static MagazineRequest createStaticMagazineRequest(List<Long> authorIds) {
        return createMagazineRequest("testing magazine", 142, LocalDate.of(2025, 1, 1), authorIds);
    }
}