package com.mobelite.publisher.api.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.publisher.api.base.ApiAssertions;
import com.mobelite.publisher.api.base.BaseTestApi;
import com.mobelite.publisher.api.constants.HttpStatusCodes;
import com.mobelite.publisher.api.factory.AuthorFactory;
import com.mobelite.publisher.api.factory.BookFactory;
import com.mobelite.publisher.api.models.Author;
import com.mobelite.publisher.api.models.Book;
import com.mobelite.publisher.api.models.request.AuthorRequest;
import com.mobelite.publisher.api.models.request.BookRequest;
import com.mobelite.publisher.api.models.response.ApiResponse;
import com.mobelite.publisher.api.models.response.PageResponse;
import com.mobelite.publisher.api.utils.ApiUtils;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static com.mobelite.publisher.api.constants.ApiEndpoints.*;
import static com.mobelite.publisher.api.constants.Schemas.API_RESPONSE_SCHEMA;
import static com.mobelite.publisher.api.constants.Schemas.BOOK_SCHEMA;

@Slf4j
@Epic("Book API")
@Feature("E2E Book Management")
public class BookApiTest extends BaseTestApi<Book, BookRequest> {

    private final BookFactory bookFactory = new BookFactory();
    private final AuthorFactory authorFactory = new AuthorFactory();
    private Long authorId; // will hold a valid author ID for books

    @Override
    protected TypeReference<ApiResponse<Book>> getItemTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected TypeReference<ApiResponse<PageResponse<Book>>> getPageTypeReference() {
        return new TypeReference<>() {};
    }

    @BeforeClass
    public void setUp() {
        init(request);
        AuthorRequest authorRequest = authorFactory.createValidAuthorRequest();
        authorId = ApiUtils.createAuthorEntity(apiClient, authorRequest, AUTHORS_BASE);
    }

    @AfterClass
    public void tearDown() {
        cleanupEntities(BOOKS_BY_ID);
        ApiUtils.deleteSharedAuthor(apiClient, authorId, AUTHOR_BY_ID);
    }

    @Test(description = "Create book with valid data")
    @Story("Create Book")
    public void createBookWithValidData() {
        BookRequest bookRequest = bookFactory.createValidBook(authorId);
        var response = create(bookRequest, BOOKS_BASE);

        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_CREATED);
        ApiResponse<Book> parsed = parseAndValidate(response, API_RESPONSE_SCHEMA, BOOK_SCHEMA);
        Book created = parsed.getData();

        Assert.assertNotNull(created.getId());
        Assert.assertEquals(created.getTitle(), bookRequest.getTitle());
        Assert.assertEquals(created.getIsbn(), bookRequest.getIsbn());
        trackCreatedEntity(created.getId());
    }

    @Test(description = "Create book with minimal data")
    @Story("Create Book")
    public void createBookWithMinimalData() {
        BookRequest minimalRequest = bookFactory.createMinimalBook(authorId);
        var response = create(minimalRequest, BOOKS_BASE);

        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_CREATED);
        ApiResponse<Book> parsed = parseAndValidate(response, API_RESPONSE_SCHEMA, BOOK_SCHEMA);
        Book created = parsed.getData();

        Assert.assertNotNull(created.getId());
        Assert.assertEquals(created.getTitle(), minimalRequest.getTitle());
        trackCreatedEntity(created.getId());
    }

    @Test(description = "Retrieve book by ID")
    @Story("Retrieve Book")
    public void getBookById() {
        BookRequest bookRequest = bookFactory.createValidBook(authorId);
        var createResponse = create(bookRequest, BOOKS_BASE);
        ApiResponse<Book> createdParsed = parseAndValidate(createResponse, API_RESPONSE_SCHEMA, BOOK_SCHEMA);
        Book created = createdParsed.getData();
        trackCreatedEntity(created.getId());

        var getResponse = getById(created.getId(), BOOKS_BY_ID);
        ApiAssertions.assertStatus(getResponse, HttpStatusCodes.STATUS_OK);
        ApiResponse<Book> retrievedParsed = parseAndValidate(getResponse, API_RESPONSE_SCHEMA, BOOK_SCHEMA);

        Assert.assertEquals(retrievedParsed.getData().getId(), created.getId());
    }

    @Test(description = "Retrieve books with pagination")
    @Story("Retrieve Book")
    public void getAllBooksWithPagination() {
        Map<String, String> queryParams = Map.of(
                "page", "0",
                "size", "5",
                "sort", "DESC"
        );

        var response = getAll(BOOKS_BASE, queryParams);
        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_OK);

        ApiResponse<PageResponse<Book>> parsed = apiClient.parseResponse(response, getPageTypeReference());
        Assert.assertNotNull(parsed.getData().getContent());
        Assert.assertTrue(parsed.getData().getContent().size() <= 5);
    }

    @Test(description = "Delete book successfully")
    @Story("Delete Book")
    public void deleteBook() {
        BookRequest bookRequest = bookFactory.createValidBook(authorId);
        var createResponse = create(bookRequest, BOOKS_BASE);
        ApiResponse<Book> createdParsed = parseAndValidate(createResponse, API_RESPONSE_SCHEMA, BOOK_SCHEMA);
        Book created = createdParsed.getData();

        var deleteResponse = delete(created.getId(), BOOKS_BY_ID);
        ApiAssertions.assertStatus(deleteResponse, HttpStatusCodes.STATUS_OK);
    }

    @Test(description = "Fail to create book with invalid data")
    @Story("Create Book")
    public void createBookWithInvalidData() {
        BookRequest invalidRequest = bookFactory.createWithInvalidISBN(authorId);
        var response = create(invalidRequest, BOOKS_BASE);

        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_BAD_REQUEST);
    }

    @Test(description = "Fail to delete non-existent book")
    @Story("Delete Book")
    public void deleteNonExistentBook() {
        Long nonExistentId = 999999L;
        var response = delete(nonExistentId, BOOKS_BY_ID);

        ApiAssertions.assertStatus(response, HttpStatusCodes.STATUS_NOT_FOUND);
    }
}