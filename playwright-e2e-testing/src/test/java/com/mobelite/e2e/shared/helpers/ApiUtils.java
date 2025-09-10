package com.mobelite.e2e.shared.helpers;


public class ApiUtils {

    public static String buildPath(String template, Object... params) {
        String path = template;
        for (Object param : params) {
            path = path.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
        }
        return path;
    }
}