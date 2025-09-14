package com.mobelite.e2e.shared.helpers;


import com.microsoft.playwright.APIResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiUtils {
   // combining the endpoint and an identifier.
    public static String buildPath(String template, Object... params) {
        String path = template;
        for (Object param : params) {
            path = path.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
        }
        return path;
    }
    public static String getResponseText(APIResponse response) {
        try {
            return response.text();
        } catch (Exception e) {
            log.warn("Failed to read response text: {}", e.getMessage(), e);
            return "";
        }
    }
}