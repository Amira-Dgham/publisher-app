# playwright-e2e-testing/Dockerfile
FROM mcr.microsoft.com/playwright/java:v1.53.0-jammy

WORKDIR /ui-automation-testing

# Copy everything into /playwright-e2e-testing
COPY . .

# Download dependencies
RUN mvn dependency:go-offline -B

# Compile project
RUN mvn compile

# Default command
CMD ["mvn", "test"]