package com.mobelite.e2e.api.core;

import static org.junit.jupiter.api.Assertions.*;

import com.microsoft.playwright.APIResponse;
import com.mobelite.e2e.api.models.ApiResponse;
import io.qameta.allure.Step;

public class ApiAssertions {

    @Step("Validate raw API response has expected status: {expectedStatus}")
    public static void assertSuccess(APIResponse httpResponse, int expectedStatus) {
        assertNotNull(httpResponse, "HTTP response should not be null");
        assertEquals(expectedStatus, httpResponse.status(),
                () -> String.format("Expected HTTP status %d but was %d",
                        expectedStatus, httpResponse.status()));
    }

    @Step("Validate response is success with expected suvccess is true: {expectedStatus}")
    public static <T> void assertSuccess(ApiResponse<?> response) {
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(),
                () -> String.format("Expected success to be true but was '%s'", response.isSuccess()));
    }

    @Step("Validate response has data")
    public static void assertHasData(ApiResponse<?> response) {
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getData(), "Response data should not be null");
    }

    @Step("Validate response message contains: {expectedMessage}")
    public static void assertMessageContains(ApiResponse<?> response, String expectedMessage) {
        assertNotNull(response.getMessage(), "Response message should not be null");
        assertTrue(response.getMessage().contains(expectedMessage),
                () -> String.format("Expected message to contain '%s' but was '%s'",
                        expectedMessage, response.getMessage()));
    }
}