package com.mobelite.publisher.api.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.publisher.api.base.ApiAssertions;
import com.mobelite.publisher.api.base.BaseTestApi;
import com.mobelite.publisher.api.constants.HttpStatusCodes;
import com.mobelite.publisher.api.factory.AuthorFactory;
import com.mobelite.publisher.api.models.Author;
import com.mobelite.publisher.api.models.request.AuthorRequest;
import com.mobelite.publisher.api.models.response.ApiResponse;
import com.mobelite.publisher.api.utils.PlaywrightSchemaValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.mobelite.publisher.api.constants.ApiEndpoints.AUTHORS_BASE;
import static com.mobelite.publisher.api.constants.ApiEndpoints.AUTHOR_BY_ID;
import static com.mobelite.publisher.api.constants.Schemas.API_RESPONSE_SCHEMA;
import static com.mobelite.publisher.api.constants.Schemas.AUTHOR_SCHEMA;


@Slf4j
public class AuthorApiTest extends BaseTestApi<AuthorRequest> {

    private final AuthorFactory  authorFactory = new AuthorFactory();

    @BeforeClass
    public void setUp() {
        // Initialize the API context from BaseTest
        init(request);
    }

    @AfterClass
    public void tearDown() {
        // Cleanup all tracked entities
        log.info("hellooo");
        cleanupEntities(AUTHOR_BY_ID);
    }

    @Test
    public void testCreateAuthor() {
        AuthorRequest authorRequest = authorFactory.createValidAuthorRequest();
        var response = create(authorRequest, AUTHORS_BASE); // endpoint can also be AUTHOR_BY_ID
        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_CREATED);
        ApiResponse<Author> parsedResponse = apiClient.parseResponse(response, new TypeReference<ApiResponse<Author>>() {});
        PlaywrightSchemaValidatorUtils.validateResponseAndData(
                parsedResponse,
                API_RESPONSE_SCHEMA,
                AUTHOR_SCHEMA,
                null
        );
        Author createdAuthor = parsedResponse.getData();
        Assert.assertNotNull(createdAuthor.getId());
        Assert.assertEquals(createdAuthor.getName(), authorRequest.getName());
        Assert.assertEquals(createdAuthor.getBirthDate(), authorRequest.getBirthDate());
        Assert.assertEquals(createdAuthor.getNationality(), authorRequest.getNationality());
        trackCreatedEntity(createdAuthor.getId());
    }
}