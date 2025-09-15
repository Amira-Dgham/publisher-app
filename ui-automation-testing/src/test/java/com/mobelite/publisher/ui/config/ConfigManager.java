package com.mobelite.publisher.api.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Getter
public final class ConfigManager {

    private static volatile ConfigManager instance;

    private final String apiBaseUrl;
    // headless = true  → runs browser in background (faster, good for CI/CD)
    // headless = false → shows browser window (useful for debugging)
    private final boolean headless;
    // JSON parser and generator for Java ( Serialization / Deserialization)
    private final ObjectMapper objectMapper;

    private ConfigManager() {
        Properties props = loadProperties();
        this.apiBaseUrl = props.getProperty("api.base.url");
        this.headless = Boolean.parseBoolean(props.getProperty("browser.headless", "true"));
        this.objectMapper = createObjectMapper();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private Properties loadProperties() {
        String environment = System.getProperty("test.env", "dev");
        Properties properties = new Properties();

        try (InputStream input = getClass().getResourceAsStream("/environments/" + environment + ".properties")) {
            if (input == null) {
                throw new RuntimeException("Environment config not found: " + environment);
            }
            properties.load(input);
            log.info("Loaded configuration for environment: {}", environment);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load environment properties for: " + environment, e);
        }
        return properties;
    }

    private ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule()) // support LocalDate, LocalDateTime
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // ignore unknown JSON fields
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false) // read timestamps as milliseconds
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // write dates as ISO, not timestamps
    }
}