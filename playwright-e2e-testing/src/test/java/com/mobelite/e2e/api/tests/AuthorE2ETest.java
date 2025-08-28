package com.mobelite.e2e.api.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.core.ApiAssertions;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import com.mobelite.e2e.api.core.BaseApiEndPoint;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import static com.mobelite.e2e.shared.constants.ApiEndpoints.AUTHORS_BASE;
import static com.mobelite.e2e.shared.constants.ApiEndpoints.AUTHOR_BY_ID;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Author Management")
@Feature("Author API")
@Story("E2E Testing")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Author API E2E Tests")
@Slf4j
public class AuthorE2ETest extends BaseApiEndPoint<Author, AuthorRequest> {

    private final AuthorFixtures authorFixtures = new AuthorFixtures();

    // ---- Implement abstract methods ----
    @Override protected String getEntityName() { return "Author"; }
    @Override protected String getItemSchema() { return "/schemas/author-schema.json"; }
    @Override protected AuthorRequest createSharedEntityRequest() { return authorFixtures.createValidAuthorRequest(); }
    @Override protected TypeReference<ApiResponse<Author>> getItemTypeReference() { return new TypeReference<>() {}; }
    @Override protected TypeReference<ApiResponse<PageResponse<Author>>> getPageTypeReference() { return new TypeReference<>() {}; }
    @Override protected String getBaseEndpoint() { return AUTHORS_BASE; }
    @Override protected String getItemByIdEndpoint() { return AUTHOR_BY_ID; }

    // -------- CREATE TESTS -------- //

    @Test
    @DisplayName("Create author with valid data")
    void createAuthorWithValidData() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = createAndValidate(request, getBaseEndpoint());
        trackForCleanup(created.getId());

        assertNotNull(created.getId());
        assertEquals(request.getName(), created.getName());
        assertEquals(request.getBirthDate(), created.getBirthDate());
        assertEquals(request.getNationality(), created.getNationality());

        log.info("Author created successfully: {}", created.getId());
    }

    @Test
    @DisplayName("Create author with minimal data")
    void createAuthorWithMinimalData() {
        AuthorRequest request = authorFixtures.createMinimalAuthorRequest();
        Author created = createAndValidate(request, getBaseEndpoint());
        trackForCleanup(created.getId());

        assertNotNull(created.getId());
        assertEquals(request.getName(), created.getName());

        log.info("Minimal author created successfully: {}", created.getId());
    }

    // -------- READ TESTS -------- //

    @Test
    @DisplayName("Retrieve shared author by ID")
    void getSharedAuthorById() {
        Author retrieved = getByIdAndValidate(sharedEntity.getId(), getItemByIdEndpoint());

        assertEquals(sharedEntity.getId(), retrieved.getId());
        assertEquals(sharedEntity.getName(), retrieved.getName());

        log.info("Successfully retrieved shared author: {}", retrieved.getId());
    }

    @Test
    @DisplayName("Retrieve authors with pagination")
    void getAllAuthorsWithPagination() {
        PageResponse<Author> page = getAllAndValidate(getBaseEndpoint());

        assertTrue(page.hasContent(), "Page should have content");
        log.info("Pagination test successful - found {} total authors", page.getTotalElements());
    }

    // -------- DELETE TESTS -------- //

    @Test
    @DisplayName("Delete author successfully")
    void deleteAuthor() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = createAndValidate(request, getBaseEndpoint());

        ApiResponse<Void> response = deleteAndValidate(created.getId(), getItemByIdEndpoint());
        ApiAssertions.assertSuccess(response);

        log.info("Author deleted successfully: {}", created.getId());
    }

    @Test
    @DisplayName("Fail to delete non-existent author")
    void deleteNonExistentAuthor() {
        Long nonExistentId = 999999L;
        ApiResponse<?> response = executeErrorRequest(delete(buildPath(getItemByIdEndpoint(), nonExistentId)), 404);

        assertFalse(response.isSuccess());
        log.info("Delete non-existent author test passed with expected error");
    }

    // -------- NEGATIVE TESTS -------- //

    @Test
    @DisplayName("Fail to create author with invalid data")
    void createAuthorWithInvalidData() {
        AuthorRequest invalid = authorFixtures.createInvalidAuthorRequest();
        ApiResponse<?> response = executeErrorRequest(post(getBaseEndpoint()).body(invalid), 400);

        assertFalse(response.isSuccess());
        log.info("Invalid author creation test passed with expected validation error");
    }

    @Test
    @DisplayName("Fail to create author with duplicate name")
    void createAuthorWithDuplicateName() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = createAndValidate(request, getBaseEndpoint());
        trackForCleanup(created.getId());

        AuthorRequest duplicateRequest = authorFixtures.createDuplicateFromAuthor(created);
        ApiResponse<?> response = executeErrorRequest(post(getBaseEndpoint()).body(duplicateRequest), 409);

        assertFalse(response.isSuccess());
        log.info("Duplicate author creation failed as expected: {}", response);
    }
}