# Author E2E Testing Framework

A simplified and focused E2E testing framework for the Author API using Playwright and JUnit 5.

## Overview

This framework provides essential E2E tests for the Author API, focusing on core CRUD operations and validation scenarios. It has been refactored to follow best practices and maintain simplicity.

## Features

- **Focused Testing**: Tests only essential Author API scenarios
- **Clean Architecture**: Well-structured with clear separation of concerns
- **Best Practices**: Proper setup/teardown, resource management, and error handling
- **Allure Reporting**: Integrated with Allure for comprehensive test reporting
- **Configuration Management**: Environment-based configuration support

## Test Scenarios

### Positive Tests
- Create author with valid data
- Create author with minimal data
- Retrieve author by ID
- Retrieve all authors with pagination
- Retrieve authors with custom pagination

### Negative Tests
- Create author with invalid data
- Retrieve non-existent author

### Data Consistency Tests
- Verify data consistency across create and retrieve operations

## Project Structure

```
src/main/java/com/mobelite/e2e/
├── api/
│   ├── client/
│   │   ├── ApiClient.java          # HTTP client abstraction
│   │   └── ApiRequestBuilder.java  # Request builder
│   ├── endpoints/
│   │   ├── BaseEndpoints.java      # Base endpoint functionality
│   │   └── AuthorEndpoints.java    # Author-specific endpoints
│   └── test/
│       └── AuthorE2ETest.java      # Main test class
├── config/
│   └── TestConfig.java             # Test configuration
├── fixtures/
│   └── AuthorFixtures.java         # Test data and setup
├── models/
│   ├── Author.java                 # Author model
│   ├── ApiResponse.java            # API response wrapper
│   ├── PageResponse.java           # Pagination response
│   └── request/
│       └── AuthorRequest.java      # Author request model
└── constants/
    ├── ApiEndpoints.java           # API endpoint constants
    └── HttpStatusCodes.java        # HTTP status codes
```

## Prerequisites

- Java 17+
- Maven 3.6+
- Spring Boot application running on localhost:8080

## Configuration

The framework automatically loads configuration from environment-specific properties files:

- `dev.properties` - Development environment (default)
- `staging.properties` - Staging environment
- `prod.properties` - Production environment

Set the environment using: `-Dtest.env=dev`

## Running Tests

### Using Maven

```bash
# Run all tests
mvn test

# Run with specific environment
mvn test -Dtest.env=staging

# Run with Allure reporting
mvn test allure:report
```

### Using IDE

Run the `AuthorE2ETest` class directly from your IDE.

## Test Execution Flow

1. **Setup**: Initialize Playwright, ApiClient, and test fixtures
2. **Test Execution**: Run individual test methods
3. **Teardown**: Clean up test data and close resources

## Best Practices Implemented

- **Resource Management**: Proper cleanup of Playwright and API resources
- **Test Isolation**: Each test creates its own test data
- **Error Handling**: Comprehensive error handling and logging
- **Validation**: Proper response validation and assertions
- **Logging**: Structured logging for debugging and monitoring

## Adding New Tests

1. Add new test methods to `AuthorE2ETest.java`
2. Follow the Arrange-Act-Assert pattern
3. Use descriptive test names and descriptions
4. Add proper cleanup in the test method if needed

## Troubleshooting

### Common Issues

1. **Connection Refused**: Ensure the Spring Boot application is running on localhost:8080
2. **Test Failures**: Check the application logs for validation errors
3. **Resource Cleanup**: Verify that test data is properly cleaned up

### Debug Mode

Enable debug logging by setting the log level to DEBUG in your IDE or Maven configuration.

## Contributing

When adding new features or tests:

1. Follow the existing code structure and patterns
2. Add proper documentation and comments
3. Ensure all tests pass before submitting changes
4. Update this README if necessary

## License

This project is part of the Publisher Management System. 