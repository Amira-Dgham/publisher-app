package com.mobelite.e2e.api.tests;

import com.mobelite.e2e.api.endpoints.AuthorApiEndPoint;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import com.mobelite.e2e.config.BaseTest;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import static com.mobelite.e2e.shared.constants.ApiEndpoints.AUTHORS_BASE;
import static com.mobelite.e2e.shared.constants.ApiEndpoints.AUTHOR_BY_ID;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Author Management")
@Feature("Author API")
@Story("E2E Testing")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Author API E2E Tests")
@Slf4j
public class AuthorE2ETest extends BaseTest {

    private final AuthorFixtures authorFixtures = new AuthorFixtures();
    private static final AuthorApiEndPoint authorApi = new AuthorApiEndPoint();

    @BeforeAll
    static void setUpAll() {
        authorApi.init(api);
    }

    @AfterEach
    void tearDownEach() {
        authorApi.cleanUpEach(AUTHOR_BY_ID);
    }

    @Test
    @DisplayName("Create author with valid data")
    void createAuthorWithValidData() {
        log.info("1 amira");
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = authorApi.createAuthor(request);

        assertNotNull(created.getId());
        assertEquals(request.getName(), created.getName());
        assertEquals(request.getBirthDate(), created.getBirthDate());
        assertEquals(request.getNationality(), created.getNationality());

        log.info("Author created successfully: {}", created.getId());
    }

    @Test
    @DisplayName("Create author with minimal data")
    void createAuthorWithMinimalData() {
        log.info("2 amira");
        AuthorRequest request = authorFixtures.createMinimalAuthorRequest();
        Author created = authorApi.createAuthor(request);

        assertNotNull(created.getId());
        assertEquals(request.getName(), created.getName());

        log.info("Minimal author created successfully: {}", created.getId());
    }

    @Test
    @DisplayName("Retrieve author by ID")
    void getAuthorById() {
        log.info("3 amira");

        Author created = authorApi.createAuthor(authorFixtures.createValidAuthorRequest());
        Author retrieved = authorApi.getAuthorById(created.getId());

        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getName(), retrieved.getName());

        log.info("Successfully retrieved author by ID: {}", retrieved.getId());
    }

    @Test
    @DisplayName("Retrieve author by name")
    void getAuthorByName() {
        log.info("4 amira");

        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = authorApi.createAuthor(request);

        Author retrieved = authorApi.getByName(request.getName());

        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getName(), retrieved.getName());

        log.info("Successfully retrieved author by name: {}", retrieved.getName());
    }

    @Test
    @DisplayName("Retrieve authors with pagination")
    void getAllAuthorsWithPagination() {
        log.info("5 amira");

        PageResponse<Author> page = authorApi.getAllAndValidate(AUTHORS_BASE);
        assertTrue(page.hasContent());
        log.info("Pagination test successful - found {} total authors", page.getTotalElements());
    }

    @Test
    @DisplayName("Delete author successfully")
    void deleteAuthor() {
        log.info("6 amira");

        Author created = authorApi.createAuthor(authorFixtures.createValidAuthorRequest());
        authorApi.deleteAuthor(created.getId());
        log.info("Author deleted successfully: {}", created.getId());
    }


    @Test
    @DisplayName("Fail to create author with invalid data")
    void createAuthorWithInvalidData() {
        log.info("7 amira");

        var invalidRequest = authorFixtures.createInvalidAuthorRequest();
        var response = authorApi.createInvalidAuthor(invalidRequest, 400);

        assertFalse(response.isSuccess());
        log.info("Invalid author creation test passed as expected");
    }

    @Test
    @DisplayName("Fail to delete non-existent author")
    void deleteNonExistentAuthor() {
        log.info("8 amira");

        Long nonExistentId = 999999L;
        var response = authorApi.deleteNonExistentAuthor(nonExistentId, 404);

        assertFalse(response.isSuccess());
        log.info("Delete non-existent author test passed with expected error");
    }

    @Test
    @DisplayName("Fail to create author with duplicate name")
    void createAuthorWithDuplicateName() {
        log.info("9 amira");

        Author created = authorApi.createAuthor(authorFixtures.createValidAuthorRequest());
        var duplicateRequest = authorFixtures.createDuplicateFromAuthor(created);

        var response = authorApi.createInvalidAuthor(duplicateRequest, 409);

        assertFalse(response.isSuccess(), "Duplicate author creation should fail");
        log.info("Duplicate author creation failed as expected");
    }
}