package com.mobelite.e2e.config;
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
public final class TestConfig {
    private static volatile TestConfig instance;

    private final String apiBaseUrl;
    private final String webBaseUrl;
    private final String databaseUrl;
    private final boolean headless;
    private final ObjectMapper objectMapper;

    private TestConfig() {
        Properties props = loadProperties();
        this.apiBaseUrl = props.getProperty("api.base.url");
        this.webBaseUrl = props.getProperty("web.base.url");
        this.databaseUrl = props.getProperty("database.url");
        this.headless = Boolean.parseBoolean(props.getProperty("browser.headless", "true"));
        this.objectMapper = createObjectMapper();
    }

    public static TestConfig getInstance() {
        if (instance == null) {
            synchronized (TestConfig.class) {
                if (instance == null) {
                    instance = new TestConfig();
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
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // Important
    }
}