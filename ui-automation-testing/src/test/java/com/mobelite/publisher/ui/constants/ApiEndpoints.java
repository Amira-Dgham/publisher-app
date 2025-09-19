package com.mobelite.publisher.ui.constants;

public final class ApiEndpoints {
    private ApiEndpoints() {} // Prevent instantiation

    public static final String AUTHORS_BASE = "/api/v1/authors";
    public static final String AUTHOR_BY_ID = AUTHORS_BASE + "/{id}";
    public static final String BOOKS_BASE = "/api/v1/books";
    public static final String BOOKS_BY_ID = BOOKS_BASE + "/{id}";
    public static final String MAGAZINES_BASE = "/api/v1/magazines";
    public static final String MAGAZINES_BY_ID = MAGAZINES_BASE + "/{id}";
}