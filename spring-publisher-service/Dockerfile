# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only necessary files for dependency caching
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build with Maven profile, skip tests
ARG ENV=dev
RUN ./mvnw clean package -P${ENV} -DskipTests -B

# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# Non-root user
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Copy built jar
COPY --from=build /app/target/*.jar app.jar

USER appuser
EXPOSE 8080

# Spring profile comes from Docker Compose or environment variable
ENTRYPOINT ["sh", "-c", "exec java -jar app.jar --spring.profiles.active=${ENV:-dev}"]