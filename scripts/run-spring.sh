#!/bin/bash

set -e

# Default values
ENV=${1:-dev}
ACTION=${2:-start}

# Paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
SPRING_PROJECT_DIR="$PROJECT_ROOT/spring-publisher-service"
ENV_CONFIG_FILE="$SPRING_PROJECT_DIR/config/.env.$ENV"
ROOT_ENV_FILE="$PROJECT_ROOT/.env"

# Environment validation
case "$ENV" in
    dev|staging|prod) ;;
    *) echo "Invalid environment: $ENV. Use dev, staging, or prod" && exit 1 ;;
esac

# Validate required config file
if [[ ! -f "$ENV_CONFIG_FILE" ]]; then
    echo "Configuration file not found: $ENV_CONFIG_FILE"
    echo "Available environments:"
    ls -1 "$SPRING_PROJECT_DIR/config/" 2>/dev/null | grep "\.env\." | sed 's/\.env\./  - /' || echo "  No config files found"
    exit 1
fi

# Load environment configuration
set -o allexport

# Load shared root .env file (optional)
if [[ -f "$ROOT_ENV_FILE" ]]; then
    source "$ROOT_ENV_FILE"
fi

# Load environment-specific config (required)
source "$ENV_CONFIG_FILE"

set +o allexport

# Set defaults and export essential variables
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-$ENV}"
export APP_PORT="${APP_PORT:-8080}"
export POSTGRES_USER="${POSTGRES_USER:-postgres}"

# Determine if monitoring profile should be used
USE_MONITORING_PROFILE=""
if [[ "$ENV" == "staging" || "$ENV" == "prod" ]]; then
    # Check if docker-compose supports profiles
    if docker compose up --help 2>/dev/null | grep -q -- '--profile'; then
        USE_MONITORING_PROFILE="--profile monitoring"
    fi
fi

cd "$PROJECT_ROOT"

# Validate docker-compose.yml exists
if [[ ! -f "$PROJECT_ROOT/docker-compose.yml" ]]; then
    echo "docker-compose.yml not found in: $PROJECT_ROOT" && exit 1
fi

# Helper function to wait for service health
wait_for_service() {
    local service_name=$1
    local health_check=$2
    local attempts=0
    local max_attempts=30
    
    while ! eval "$health_check" >/dev/null 2>&1; do
        sleep 2
        attempts=$((attempts + 1))
        
        if [[ $attempts -gt $max_attempts ]]; then
            echo "Timeout waiting for $service_name"
            exit 1
        fi
    done
}

# Execute actions
case "$ACTION" in
    start)
        echo "🚀 Starting $ENV environment..."
        
        # Start database
        docker compose up -d db-publisher-service
        
        # Wait for database
        wait_for_service "db-publisher-service" \
            "docker compose exec db-publisher-service pg_isready -U '$POSTGRES_USER'"
        
        # Start Spring Boot application
        docker compose up -d --build $USE_MONITORING_PROFILE spring-publisher-service
        
        # Wait for Spring Boot
        wait_for_service "spring-publisher-service" \
            "curl -s -f 'http://localhost:${APP_PORT}/actuator/health'"
        
        echo "Started at http://localhost:${APP_PORT}"
        ;;

    stop)
        docker compose down --remove-orphans
        echo "Stopped"
        ;;

    restart)
        "$0" "$ENV" stop
        "$0" "$ENV" start
        ;;

    logs)
        local service="${3:-spring}"
        case "$service" in
            db|database) docker compose logs -f db-publisher-service ;;
            *) docker compose logs -f spring-publisher-service ;;
        esac
        ;;

    build)
        docker compose build spring-publisher-service
        echo "Built"
        ;;

    clean)
        docker compose down --remove-orphans --volumes
        docker system prune -f
        echo "Cleaned"
        ;;

    shell)
        local service="${3:-spring}"
        case "$service" in
            db|database) docker compose exec db-publisher-service psql -U "$POSTGRES_USER" ;;
            *) docker compose exec spring-publisher-service /bin/bash || docker compose exec spring-publisher-service /bin/sh ;;
        esac
        ;;

    test)
        docker compose exec spring-publisher-service ./mvnw test
        ;;

    status)
        docker compose ps
        if curl -s -f "http://localhost:${APP_PORT}/actuator/health" >/dev/null 2>&1; then
            echo "Healthy: http://localhost:${APP_PORT}"
        else
            echo "Not responding"
        fi
        ;;

    *)
        exit 1
        ;;
esac