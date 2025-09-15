package com.mobelite.publisher.ui.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Getter
public final class ConfigManager {

    private static volatile ConfigManager instance;

    private final String uiBaseUrl;
    // headless = true  → runs browser in background (faster, good for CI/CD)
    // headless = false → shows browser window (useful for debugging)
    private final boolean headless;

    private ConfigManager() {
        Properties props = loadProperties();
        this.uiBaseUrl = props.getProperty("ui.base.url");
        this.headless = Boolean.parseBoolean(props.getProperty("browser.headless", "true"));
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


}