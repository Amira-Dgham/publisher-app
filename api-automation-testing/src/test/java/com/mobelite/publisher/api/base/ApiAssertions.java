package com.mobelite.publisher.api.base;

import com.microsoft.playwright.APIResponse;
import com.mobelite.publisher.api.models.response.ApiResponse;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

@Slf4j
public class ApiAssertions {

    @Step("Validate raw HTTP response has status: {expectedStatus}")
    public static void assertStatus(APIResponse httpResponse, int expectedStatus) {
        Assert.assertNotNull(httpResponse, "HTTP response should not be null");
        Assert.assertEquals(httpResponse.status(), expectedStatus,
                String.format("Expected HTTP status %d but was %d", expectedStatus, httpResponse.status()));
    }

    @Step("Validate API response success is true")
    public static <T> void assertSuccess(ApiResponse<T> response) {
        Assert.assertNotNull(response, "API response should not be null");
        Assert.assertTrue(response.isSuccess(),
                String.format("Expected success=true but was '%s'", response.isSuccess()));
    }

    @Step("Validate API response has non-null data")
    public static <T> void assertHasData(ApiResponse<T> response) {
        Assert.assertNotNull(response, "API response should not be null");
        Assert.assertNotNull(response.getData(), "Response data should not be null");
    }

    @Step("Validate API response message contains: {expectedMessage}")
    public static <T> void assertMessageContains(ApiResponse<T> response, String expectedMessage) {
        Assert.assertNotNull(response, "API response should not be null");
        Assert.assertNotNull(response.getMessage(), "Response message should not be null");
        Assert.assertTrue(response.getMessage().contains(expectedMessage),
                String.format("Expected message to contain '%s' but was '%s'",
                        expectedMessage, response.getMessage()));
    }

    @Step("Validate API response data equals expected value")
    public static <T> void assertDataEquals(ApiResponse<T> response, T expectedData) {
        Assert.assertNotNull(response, "API response should not be null");
        Assert.assertEquals(response.getData(), expectedData,
                String.format("Expected data '%s' but was '%s'", expectedData, response.getData()));
    }
}