package com.mobelite.e2e.api.fixtures;

import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.endpoints.AuthorEndpoints;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class AuthorFixtures {

    private final AuthorEndpoints authorEndpoints;
    @Getter
    private final List<Author> createdAuthors = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong authorCounter = new AtomicLong(1);
    private static final String TEST_AUTHOR_PREFIX = "TEST_AUTHOR_";

    private static final Faker faker = new Faker();

    public AuthorFixtures(ApiClient apiClient) {
        this.authorEndpoints = new AuthorEndpoints(apiClient);
    }

    // ---------------------- AUTHOR REQUEST FACTORY ---------------------- //

    @Step("Create author request")
    public AuthorRequest createAuthorRequest(String name, LocalDate birthDate, String nationality) {
        return AuthorRequest.builder()
                .name(name)
                .birthDate(birthDate)
                .nationality(nationality)
                .build();
    }


    private static LocalDate generateRandomBirthDate(int minAge, int maxAge) {
        // Current year
        int currentYear = LocalDate.now().getYear();

        // Calculate random year of birth
        int randomYear = ThreadLocalRandom.current().nextInt(currentYear - maxAge, currentYear - minAge + 1);

        // Random month and day
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int day = ThreadLocalRandom.current().nextInt(1, Month.of(month).length(false) + 1);

        return LocalDate.of(randomYear, month, day);
    }

    @Step("Create valid author request with Faker")
    public AuthorRequest createValidAuthorRequest() {
        String suffix = faker.name().firstName() + "_" + authorCounter.getAndIncrement();
        LocalDate birthDate = generateRandomBirthDate(20, 70);
        String nationality = faker.country().name();
        return createAuthorRequest(suffix, birthDate, nationality);
    }
    @Step("Create minimal author request with Faker")
    public AuthorRequest createMinimalAuthorRequest() {
        String name = TEST_AUTHOR_PREFIX + faker.name().firstName() + "_" + authorCounter.getAndIncrement();
        // minimal author can have null birthDate/nationality
        return createAuthorRequest(name, null, null);
    }

    @Step("Create invalid author request with Faker")
    public AuthorRequest createInvalidAuthorRequest() {
        // purposely invalid data
        String name = ""; // invalid
        LocalDate birthDate = LocalDate.now().plusDays(1); // future date
        String nationality = faker.lorem().characters(60); // too long
        return createAuthorRequest(name, birthDate, nationality);
    }

    // ---------------------- AUTHOR CREATION ---------------------- //

    @Step("Create author and register for cleanup")
    public Author createAuthorAndRegisterForCleanup(AuthorRequest request) {
        Author author = authorEndpoints.createAuthorAndValidateStructure(request);
        registerAuthorForCleanup(author);
        return author;
    }

    @Step("Setup multiple test authors with Faker")
    public void setupTestAuthors(int count) {
        for (int i = 0; i < count; i++) {
            try {
                createAuthorAndRegisterForCleanup(createValidAuthorRequest());
            } catch (Exception e) {
                log.warn("Failed to create test author #{}: {}", i + 1, e.getMessage());
            }
        }
        log.info("Setup complete. Authors to cleanup: {}", createdAuthors.size());
    }

    // ---------------------- CLEANUP ---------------------- //

    @Step("Register author for cleanup")
    public void registerAuthorForCleanup(Author author) {
        if (author != null && author.getId() != null) {
            synchronized (createdAuthors) {
                createdAuthors.add(author);
                log.debug("Registered author {} for cleanup. Total registered: {}", author.getId(), createdAuthors.size());
            }
        }
    }

    @Step("Cleanup all registered authors")
    public void cleanupAllAuthors() {
        if (createdAuthors.isEmpty()) {
            log.info("No authors to cleanup");
            return;
        }
        List<Author> failedDeletes = new ArrayList<>();
        synchronized (createdAuthors) {
            Iterator<Author> iterator = createdAuthors.iterator();
            while (iterator.hasNext()) {
                Author author = iterator.next();
                if (author == null || author.getId() == null) {
                    iterator.remove();
                    continue;
                }
                try {
                    ApiResponse<Void> deleteResponse = authorEndpoints.deleteAuthor(author.getId());
                    if (deleteResponse.isSuccess()) {
                        iterator.remove();
                        log.info("Deleted author {}", author.getId());
                    } else {
                        log.info("Author {} not found, removing from cleanup list", author.getId());
                        iterator.remove();
                    }
                } catch (Exception e) {
                    log.error("Failed to delete author {}: {}", author.getId(), e.getMessage(), e);
                    failedDeletes.add(author);
                }
            }
        }
        if (!failedDeletes.isEmpty()) {
            log.warn("Failed to delete {} authors: {}", failedDeletes.size(),
                    failedDeletes.stream().map(Author::getId).toList());
        }
        log.info("Cleanup complete. Remaining authors: {}", getCleanupCount());
    }

    @Step("Remove author from cleanup list")
    public void removeAuthorFromCleanup(Author author) {
        if (author != null && author.getId() != null) {
            synchronized (createdAuthors) {
                createdAuthors.removeIf(a -> a != null && a.getId() != null && a.getId().equals(author.getId()));
            }
        }
    }

    @Step("Remove author from cleanup list by ID")
    public void removeAuthorFromCleanup(Long authorId) {
        if (authorId != null) {
            synchronized (createdAuthors) {
                createdAuthors.removeIf(a -> a != null && a.getId() != null && a.getId().equals(authorId));
            }
        }
    }

    public int getCleanupCount() {
        synchronized (createdAuthors) {
            return createdAuthors.size();
        }
    }


    @Step("Force cleanup specific author")
    public boolean forceCleanupAuthor(Long authorId) {
        if (authorId == null) {
            return false;
        }

        try {
            log.debug("Force cleaning author with ID: {}", authorId);
            authorEndpoints.deleteAuthorAndValidateStructure(authorId);
            removeAuthorFromCleanup(authorId);
            log.info("Force cleanup successful for author: {}", authorId);
            return true;
        } catch (Exception e) {
            log.error("Force cleanup failed for author {}: {}", authorId, e.getMessage(), e);
            return false;
        }
    }

    @Step("Get list of authors pending cleanup")
    public List<Long> getAuthorsPendingCleanup() {
        synchronized (createdAuthors) {
            return createdAuthors.stream()
                    .filter(author -> author != null && author.getId() != null)
                    .map(Author::getId)
                    .toList();
        }
    }

}