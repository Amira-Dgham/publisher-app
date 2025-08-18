package com.mobelite.e2e.config;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class TestEnvironmentConfig {

    private static final String DEFAULT_ENV = "dev";
    private static Properties envProperties;

    static {
        loadEnvironmentConfig();
    }

    private static void loadEnvironmentConfig() {
        String env = System.getProperty("test.env", DEFAULT_ENV);
        envProperties = new Properties();

        try (InputStream input = TestEnvironmentConfig.class.getClassLoader()
                .getResourceAsStream("environments/" + env + ".properties")) {
            if (input != null) {
                envProperties.load(input);
            } else {
                throw new RuntimeException("Environment config not found: " + env);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load environment config", e);
        }
    }

    public static String getApiBaseUrl() {
        return envProperties.getProperty("api.base.url");
    }

    public static String getWebBaseUrl() {
        return envProperties.getProperty("web.base.url");
    }

    public static String getDatabaseUrl() {
        return envProperties.getProperty("database.url");
    }

    public static boolean isLocalEnvironment() {
        return "local".equals(System.getProperty("test.env", DEFAULT_ENV));
    }
}