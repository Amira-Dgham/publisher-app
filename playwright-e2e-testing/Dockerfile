# Use the official Playwright image for Java
FROM mcr.microsoft.com/playwright/java:v1.40.0

WORKDIR /e2e

# Copy Maven project files
COPY pom.xml ./
COPY src ./src

# Download dependencies and build tests
RUN mvn dependency:go-offline -B
RUN mvn compile

# Optionally, run tests on container start (customize as needed)
CMD ["mvn", "test"]