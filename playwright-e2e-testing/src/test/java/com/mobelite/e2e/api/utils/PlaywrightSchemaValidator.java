package com.mobelite.e2e.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobelite.e2e.config.TestConfig;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class PlaywrightSchemaValidator {

    private static final ObjectMapper mapper = TestConfig.getInstance().getObjectMapper();

    public static void validateResponseAndData(Object response,
                                               String responseSchemaPath,
                                               String dataSchemaPath,
                                               String contentSchemaPath) {
        try {
            // Serialize response object to JSON
            String json = mapper.writeValueAsString(response);
            JSONObject jsonResponse = new JSONObject(json);

            // 1. Validate wrapper (ApiResponse)
            validateAgainstSchema(jsonResponse, responseSchemaPath);

            // 2. Validate "data" if schema is provided
            if (dataSchemaPath != null && jsonResponse.has("data") && !jsonResponse.isNull("data")) {
                Object dataNode = jsonResponse.get("data");

                if (dataNode instanceof JSONObject jsonData) {
                    System.out.println("[INFO] Validating 'data' object against schema: " + dataSchemaPath);
                    validateAgainstSchema(jsonData, dataSchemaPath);

                    // 3. If "content" exists and contentSchemaPath is provided → validate each element
                    if (contentSchemaPath != null && jsonData.has("content")) {
                        JSONArray contentArray = jsonData.getJSONArray("content");
                        for (int i = 0; i < contentArray.length(); i++) {
                            System.out.println("[DEBUG] Validating 'content' element index " + i +
                                    " against schema: " + contentSchemaPath);
                            validateAgainstSchema(contentArray.getJSONObject(i), contentSchemaPath);
                        }
                    }
                } else if (dataNode instanceof JSONArray jsonArray) {
                    System.out.println("[INFO] Validating array of 'data' objects against schema: " + dataSchemaPath);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        validateAgainstSchema(jsonArray.getJSONObject(i), dataSchemaPath);
                    }
                } else {
                    System.out.println("[WARN] 'data' field is neither JSONObject nor JSONArray. Skipping schema validation for 'data'");
                }
            } else {
                System.out.println("[INFO] No 'data' field to validate or dataSchemaPath is null.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Schema validation failed", e);
        }
    }

    /**
     * Validates a JSON object against a schema.
     */
    public static void validateAgainstSchema(JSONObject json, String schemaPath) {
        try (InputStream schemaStream = PlaywrightSchemaValidator.class.getResourceAsStream(schemaPath)) {
            if (schemaStream == null) {
                throw new IllegalArgumentException("Schema not found: " + schemaPath);
            }
            JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
            Schema schema = SchemaLoader.load(rawSchema);
            schema.validate(json); // throws ValidationException if invalid
        } catch (Exception e) {
            throw new RuntimeException("Schema validation failed for schema: " + schemaPath, e);
        }
    }
}