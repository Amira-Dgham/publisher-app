package com.mobelite.e2e.shared.constants;

public final class ApiEndpoints {
    private ApiEndpoints() {} // Prevent instantiation

    public static final String AUTHORS_BASE = "/api/v1/authors";
    public static final String AUTHOR_BY_ID = AUTHORS_BASE + "/{id}";
}