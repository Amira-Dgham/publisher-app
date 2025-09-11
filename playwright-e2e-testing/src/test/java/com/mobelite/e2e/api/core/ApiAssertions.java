package com.mobelite.e2e.api.core;

import static org.junit.jupiter.api.Assertions.*;

import com.microsoft.playwright.APIResponse;
import com.mobelite.e2e.api.models.response.ApiResponse;
import io.qameta.allure.Step;

public class ApiAssertions {

    @Step("Validate raw HTTP response has status: {expectedStatus}")
    public static void assertStatus(APIResponse httpResponse, int expectedStatus) {
        assertNotNull(httpResponse, "HTTP response should not be null");
        assertEquals(expectedStatus, httpResponse.status(),
                () -> String.format("Expected HTTP status %d but was %d",
                        expectedStatus, httpResponse.status()));
    }

    @Step("Validate API response success is true")
    public static <T> void assertSuccess(ApiResponse<T> response) {
        assertNotNull(response, "API response should not be null");
        assertTrue(response.isSuccess(),
                () -> String.format("Expected success=true but was '%s'", response.isSuccess()));
    }

    @Step("Validate API response has non-null data")
    public static <T> void assertHasData(ApiResponse<T> response) {
        assertNotNull(response, "API response should not be null");
        assertNotNull(response.getData(), "Response data should not be null");
    }

    @Step("Validate API response message contains: {expectedMessage}")
    public static <T> void assertMessageContains(ApiResponse<T> response, String expectedMessage) {
        assertNotNull(response, "API response should not be null");
        assertNotNull(response.getMessage(), "Response message should not be null");
        assertTrue(response.getMessage().contains(expectedMessage),
                () -> String.format("Expected message to contain '%s' but was '%s'",
                        expectedMessage, response.getMessage()));
    }

    @Step("Validate API response data equals expected value")
    public static <T> void assertDataEquals(ApiResponse<T> response, T expectedData) {
        assertNotNull(response, "API response should not be null");
        assertEquals(expectedData, response.getData(),
                () -> String.format("Expected data '%s' but was '%s'", expectedData, response.getData()));
    }
}