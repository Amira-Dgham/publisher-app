package com.mobelite.publisher.api.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.publisher.api.base.ApiAssertions;
import com.mobelite.publisher.api.base.BaseTestApi;
import com.mobelite.publisher.api.constants.HttpStatusCodes;
import com.mobelite.publisher.api.factory.AuthorFactory;
import com.mobelite.publisher.api.models.Author;
import com.mobelite.publisher.api.models.request.AuthorRequest;
import com.mobelite.publisher.api.models.response.ApiResponse;
import com.mobelite.publisher.api.models.response.PageResponse;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static com.mobelite.publisher.api.constants.ApiEndpoints.AUTHORS_BASE;
import static com.mobelite.publisher.api.constants.ApiEndpoints.AUTHOR_BY_ID;
import static com.mobelite.publisher.api.constants.Schemas.API_RESPONSE_SCHEMA;
import static com.mobelite.publisher.api.constants.Schemas.AUTHOR_SCHEMA;

@Slf4j
@Epic("Author API")
@Feature("E2E Author Management")
public class AuthorApiTest extends BaseTestApi<Author, AuthorRequest> {

    private final AuthorFactory authorFactory = new AuthorFactory();

    @Override
    protected TypeReference<ApiResponse<Author>> getItemTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected TypeReference<ApiResponse<PageResponse<Author>>> getPageTypeReference() {
        return new TypeReference<>() {};
    }

    @BeforeClass
    public void setUp() {
        init(request);
    }

    @AfterClass
    public void tearDown() {
        cleanupEntities(AUTHOR_BY_ID);
    }

    @Test(description = "Create author with valid data")
    @Story("Create author")
    public void createAuthorWithValidData() {
        AuthorRequest req = authorFactory.createValidAuthorRequest();
        var response = create(req, AUTHORS_BASE);

        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_CREATED);
        ApiResponse<Author> parsed = parseAndValidate(response, API_RESPONSE_SCHEMA, AUTHOR_SCHEMA);

        Author created = parsed.getData();
        ApiAssertions.assertHasData(parsed);
        Assert.assertEquals(created.getName(), req.getName());
        trackCreatedEntity(created.getId());
    }

    @Test(description = "Create author with minimal data")
    @Story("Create author")
    public void createAuthorWithMinimalData() {
        AuthorRequest req = authorFactory.createMinimalAuthorRequest();
        var response = create(req, AUTHORS_BASE);

        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_CREATED);
        ApiResponse<Author> parsed = parseAndValidate(response, API_RESPONSE_SCHEMA, AUTHOR_SCHEMA);

        Assert.assertNotNull(parsed.getData().getId());
        trackCreatedEntity(parsed.getData().getId());
    }

    @Test(description = "Retrieve author by ID")
    @Story("Retrieve author")
    public void getAuthorById() {
        AuthorRequest req = authorFactory.createValidAuthorRequest();
        var createResp = create(req, AUTHORS_BASE);
        ApiResponse<Author> created = parseAndValidate(createResp, API_RESPONSE_SCHEMA, AUTHOR_SCHEMA);
        trackCreatedEntity(created.getData().getId());

        var response = getById(created.getData().getId(), AUTHOR_BY_ID);
        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_OK);

        ApiResponse<Author> parsed = parseAndValidate(response, API_RESPONSE_SCHEMA, AUTHOR_SCHEMA);
        Assert.assertEquals(parsed.getData().getId(), created.getData().getId());
    }

    @Test(description = "Retrieve authors with pagination")
    @Story("Retrieve author")
    public void getAllAuthorsWithPagination() {
        Map<String, String> queryParams = Map.of(
                "page", "0",
                "size", "5",
                "sort", "DESC"
        );

        var response = getAll(AUTHORS_BASE,queryParams);
        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_OK);
        // PageResponse validation
        ApiResponse<PageResponse<Author>> parsed = apiClient.parseResponse(response, getPageTypeReference());
        Assert.assertNotNull(parsed.getData().getContent());
        Assert.assertTrue(parsed.getData().getContent().size() <= 5, "Page size should be max 5");

    }

    @Test(description = "Delete author successfully")
    @Story("Delete author")
    public void deleteAuthor() {
        AuthorRequest req = authorFactory.createValidAuthorRequest();
        var createResp = create(req, AUTHORS_BASE);
        ApiResponse<Author> created = parseAndValidate(createResp, API_RESPONSE_SCHEMA, AUTHOR_SCHEMA);

        var response = delete(created.getData().getId(), AUTHOR_BY_ID);
        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_OK);
    }

    @Test(description = "Fail to create author with invalid data")
    @Story("Validation errors")
    public void createAuthorWithInvalidData() {
        AuthorRequest req = authorFactory.createInvalidAuthorRequest();
        var response = create(req, AUTHORS_BASE);
        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_BAD_REQUEST);
    }

    @Test(description = "Fail to delete non-existent author")
    @Story("Validation errors")
    public void deleteNonExistentAuthor() {
        var response = delete(999999L, AUTHOR_BY_ID);
        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_NOT_FOUND);
    }

    @Test(description = "Fail to create author with duplicate name")
    @Story("Validation errors")
    public void createAuthorWithDuplicateName() {
        AuthorRequest req = authorFactory.createValidAuthorRequest();
        var first = create(req, AUTHORS_BASE);
        ApiResponse<Author> created = parseAndValidate(first, API_RESPONSE_SCHEMA, AUTHOR_SCHEMA);
        trackCreatedEntity(created.getData().getId());

        var duplicateReq = authorFactory.createDuplicateFromAuthor(created.getData());
        var duplicateResp = create(duplicateReq, AUTHORS_BASE);
        ApiAssertions.assertStatus(duplicateResp, HttpStatusCodes.STATUS_CONFLICT);
    }
}