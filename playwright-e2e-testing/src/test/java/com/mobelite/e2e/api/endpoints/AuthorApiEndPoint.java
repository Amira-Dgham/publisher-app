package com.mobelite.e2e.api.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.core.BaseApiEndPoint;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import lombok.extern.slf4j.Slf4j;


import static com.mobelite.e2e.shared.constants.ApiEndpoints.AUTHORS_BASE;

@Slf4j
public class AuthorApiEndPoint extends BaseApiEndPoint<Author, AuthorRequest> {

    @Override
    protected String getEntityName() {
        return "Author";
    }

    @Override
    protected String getItemSchema() {
        return "/schemas/author-schema.json";
    }

    @Override
    protected TypeReference<ApiResponse<Author>> getItemTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected TypeReference<ApiResponse<PageResponse<Author>>> getPageTypeReference() {
        return new TypeReference<>() {};
    }


    public Author getByName(String name) {
        // Assuming the API supports filtering by name via query param ?name=
        // If not, this would need adjustment (e.g., fetch all pages and filter client-side)
        String searchEndpoint = AUTHORS_BASE + "?name=" + name;
        PageResponse<Author> page = getAllAndValidate(searchEndpoint);
        if (page.getContent().isEmpty()) {
            return null;
        }
        return page.getContent().get(0);
    }

}