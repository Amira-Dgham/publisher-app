# Build stage - using official Maven image with exact tag
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Create non-root user for build process
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# 1. Copy Maven wrapper and configuration files first for better caching
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# 2. Download dependencies (this layer will cache as long as pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# 3. Copy source code
COPY src ./src

# Build with profile from build arg, skip tests in Docker build
ARG ENV=dev
RUN ./mvnw clean package -P${ENV} -DskipTests -B

# Production stage - using official Eclipse Temurin JRE
FROM eclipse-temurin:17-jre AS production

# Install dumb-init for proper signal handling
RUN apt-get update && apt-get install -y --no-install-recommends \
    dumb-init \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user (Debian syntax)
RUN groupadd -r -g 1001 appgroup && \
    useradd -r -g appgroup -u 1001 -s /sbin/nologin -M appuser

# Set working directory
WORKDIR /app

# Copy JAR file from build stage
COPY --from=build --chown=appuser:appgroup /app/target/*.jar app.jar

# Switch to non-root user
USER appuser

# Expose port (documentation purpose)
EXPOSE 8080

# Use dumb-init and pass ENV at runtime
ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "exec java -jar app.jar --spring.profiles.active=${ENV}"]