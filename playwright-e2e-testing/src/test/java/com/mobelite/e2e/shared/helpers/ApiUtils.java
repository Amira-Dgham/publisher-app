package com.mobelite.e2e.shared.helpers;


public final class ApiUtils {

    private ApiUtils() {
        // utility class, prevent instantiation
    }

    /**
     * Replace placeholders in an endpoint template with actual params.
     * Example: buildPath("/authors/{id}", 123) -> "/authors/123"
     */
    public static String buildPath(String template, Object... params) {
        String path = template;
        for (Object param : params) {
            path = path.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
        }
        return path;
    }
}