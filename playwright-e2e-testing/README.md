# E2E Testing Framework for Publisher Management System

This package provides a comprehensive E2E testing framework for testing the Publisher Management System API endpoints using Playwright and JUnit 5.

## 🏗️ **Architecture Overview**

The E2E testing framework follows a layered architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                    Test Classes                             │
│              (AuthorE2ETest, etc.)                         │
├─────────────────────────────────────────────────────────────┤
│                    Fixtures                                 │
│              (AuthorFixtures, etc.)                         │
├─────────────────────────────────────────────────────────────┤
│                    Endpoints                                │
│              (AuthorEndpoints, etc.)                        │
├─────────────────────────────────────────────────────────────┤
│                    BaseEndpoints                            │
│              (Common HTTP operations)                       │
├─────────────────────────────────────────────────────────────┤
│                    ApiClient                                │
│              (Low-level HTTP client)                        │
└─────────────────────────────────────────────────────────────┘
```

## 📁 **Package Structure**

```
com.mobelite.e2e/
├── api/
│   ├── client/
│   │   ├── ApiClient.java              # Low-level HTTP client
│   │   ├── ApiRequestBuilder.java      # Request builder
│   │   └── api/
│   │       ├── AuthorApiClient.java    # High-level author client
│   │       ├── BookApiClient.java      # High-level book client
│   │       └── ...
│   ├── endpoints/
│   │   ├── BaseEndpoints.java          # Common endpoint operations
│   │   ├── AuthorEndpoints.java        # Author-specific endpoints
│   │   └── ...
│   └── test/
│       └── AuthorE2ETest.java         # Comprehensive test examples
├── fixtures/
│   ├── AuthorFixtures.java             # Author test data management
│   └── ...
├── models/
│   ├── Author.java                     # Author entity model
│   ├── Book.java                       # Book entity model
│   ├── Magazine.java                   # Magazine entity model
│   ├── Publication.java                # Publication base model
│   ├── PageResponse.java               # Pagination response model
│   ├── ApiResponse.java                # API response wrapper
│   └── request/
│       ├── AuthorRequest.java          # Author request DTO
│       ├── BookRequest.java            # Book request DTO
│       └── MagazineRequest.java        # Magazine request DTO
└── config/
    └── TestEnvironmentConfig.java      # Environment configuration
```

## 🚀 **Quick Start**

### 1. **Environment Setup**

Ensure your Spring Boot service is running and accessible at the configured URL:

```properties
# src/main/resources/environments/dev.properties
api.base.url=http://localhost:8080
web.base.url=http://localhost:4200
```

## 📊 **Allure Reporting**

The framework integrates with Allure for comprehensive test reporting:

```java
@Step("Create author via endpoint")
public ApiResponse<Author> createAuthor(AuthorRequest authorRequest) {
    // Method implementation
}

@Epic("Author Management")
@Feature("Author API")
@Story("E2E Testing")
public class AuthorE2ETest {
    // Test class
}
```

## 🔍 **Debugging and Troubleshooting**

### **Logging**
- All operations are logged with appropriate levels
- Request/response details are captured
- Error scenarios are logged with context

### **Validation**
- Response status codes are validated
- Response structure is verified
- Data integrity is checked

### **Error Handling**
- Graceful error handling with meaningful messages
- Fallback error responses for parsing failures
- Comprehensive error context for debugging

## 🚀 **Running Tests**

### **Maven Commands**

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthorE2ETest

# Run with specific environment
mvn test -Dtest.env=dev

# Run with Allure reporting
mvn test allure:report
```

### **Environment Configuration**

```bash
# Set environment
export TEST_ENV=dev

# Or via Maven
mvn test -Dtest.env=staging
```

## 📈 **Best Practices**

### **1. Test Organization**
- Group related tests using `@Epic`, `@Feature`, `@Story`
- Use descriptive test names with `@DisplayName`
- Organize tests by functionality and scenario type

### **2. Data Management**
- Always clean up test data in `@AfterEach`
- Use fixtures for consistent test data
- Validate data state before and after operations

### **3. Assertions**
- Use specific assertions for better error messages
- Validate both positive and negative scenarios
- Check data integrity across operations

### **4. Error Handling**
- Test error scenarios explicitly
- Validate error response structure
- Ensure proper error codes and messages


## 📚 **Additional Resources**

- [Playwright Documentation](https://playwright.dev/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Allure Framework](https://docs.qameta.io/allure/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/) 