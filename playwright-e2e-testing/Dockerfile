# playwright-e2e-testing/Dockerfile
FROM mcr.microsoft.com/playwright/java:v1.53.0-jammy

WORKDIR /playwright-e2e-testing

# Copy everything into /e2e
COPY . .

# Download dependencies
RUN mvn dependency:go-offline -B
RUN mvn compile

# Default command (can be overridden in docker-compose run)
CMD ["mvn", "test"]