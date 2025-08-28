package com.mobelite.e2e.api.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.core.BaseApiEndPoint;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import lombok.extern.slf4j.Slf4j;


import static com.mobelite.e2e.shared.constants.ApiEndpoints.AUTHORS_BASE;
import static com.mobelite.e2e.shared.constants.ApiEndpoints.AUTHOR_BY_ID;

@Slf4j
public class AuthorApiEndPoint extends BaseApiEndPoint<Author, AuthorRequest> {

    private final AuthorFixtures authorFixtures = new AuthorFixtures();

    @Override
    protected String getEntityName() {
        return "Author";
    }

    @Override
    protected String getItemSchema() {
        return "/schemas/author-schema.json";
    }

    @Override
    protected AuthorRequest createSharedEntityRequest() {
        return authorFixtures.createValidAuthorRequest();
    }

    @Override
    protected TypeReference<ApiResponse<Author>> getItemTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected TypeReference<ApiResponse<PageResponse<Author>>> getPageTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    public String getBaseEndpoint() {
        return AUTHORS_BASE;
    }

    @Override
    public String getItemByIdEndpoint() {
        return AUTHOR_BY_ID;
    }

    public Author getByName(String name) {
        // Assuming the API supports filtering by name via query param ?name=
        // If not, this would need adjustment (e.g., fetch all pages and filter client-side)
        String searchEndpoint = getBaseEndpoint() + "?name=" + name;
        PageResponse<Author> page = getAllAndValidate(searchEndpoint);
        if (page.getContent().isEmpty()) {
            return null;
        }
        return page.getContent().get(0);
    }

}