package com.mobelite.publisher.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobelite.publisher.api.config.ConfigManager;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class PlaywrightSchemaValidatorUtils {

    private static final ObjectMapper mapper = ConfigManager.getInstance().getObjectMapper();

    /**
     * Validates a response object against wrapper and data/content schemas.
     */
    public static void validateResponseAndData(Object response,
                                               String responseSchemaPath,
                                               String dataSchemaPath,
                                               String contentSchemaPath) {
        try {
            // Convert response object to JSONObject
            String json = mapper.writeValueAsString(response);
            JSONObject jsonResponse = new JSONObject(json);

            // Validate wrapper (ApiResponse)
            validateAgainstSchema(jsonResponse, responseSchemaPath);
            log.info("Wrapper schema validation passed: {}", responseSchemaPath);

            // Validate "data" field if provided
            if (dataSchemaPath != null && jsonResponse.has("data") && !jsonResponse.isNull("data")) {
                Object dataNode = jsonResponse.get("data");
                validateDataNode(dataNode, dataSchemaPath, contentSchemaPath);
            } else {
                log.info("No 'data' field to validate or dataSchemaPath is null.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Schema validation failed", e);
        }
    }

    /**
     * Validates a JSONObject or JSONArray data node.
     */
    private static void validateDataNode(Object dataNode, String dataSchemaPath, String contentSchemaPath) {
        if (dataNode instanceof JSONObject jsonData) {
            log.info("Validating 'data' object against schema: {}", dataSchemaPath);
            validateAgainstSchema(jsonData, dataSchemaPath);

            if (contentSchemaPath != null && jsonData.has("content")) {
                JSONArray contentArray = jsonData.getJSONArray("content");
                for (int i = 0; i < contentArray.length(); i++) {
                    log.debug("Validating 'content' element index {} against schema: {}", i, contentSchemaPath);
                    validateAgainstSchema(contentArray.getJSONObject(i), contentSchemaPath);
                }
            }

        } else if (dataNode instanceof JSONArray jsonArray) {
            log.info("Validating array of 'data' objects against schema: {}", dataSchemaPath);
            for (int i = 0; i < jsonArray.length(); i++) {
                validateAgainstSchema(jsonArray.getJSONObject(i), dataSchemaPath);
            }

        } else {
            log.warn("'data' field is neither JSONObject nor JSONArray. Skipping schema validation.");
        }
    }

    /**
     * Validates a JSON object against a schema file.
     */
    public static void validateAgainstSchema(JSONObject json, String schemaPath) {
        try (InputStream schemaStream = PlaywrightSchemaValidatorUtils.class.getResourceAsStream(schemaPath)) {
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