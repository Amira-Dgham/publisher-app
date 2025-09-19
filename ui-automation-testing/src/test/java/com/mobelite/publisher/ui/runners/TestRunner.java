package com.mobelite.publisher.ui.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.mobelite.publisher.ui.steps","com.mobelite.publisher.ui.hooks"},
        plugin = {
                "pretty", // readable console output
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm" // Allure plugin
        },
        monochrome = true

)
public class TestRunner extends AbstractTestNGCucumberTests {
    // No extra code needed, TestNG handles execution
}