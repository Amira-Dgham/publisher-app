#!/bin/bash
set -e

# Default values
ENV=${1:-dev}
ACTION=${2:-start}

# Validate environment
if [[ ! "$ENV" =~ ^(dev|staging|prod)$ ]]; then
    echo "Invalid ENV: $ENV. Allowed: dev, staging, prod"
    exit 1
fi

# Paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
SPRING_PROJECT_DIR="$PROJECT_ROOT/spring-publisher-service"
ENV_CONFIG_FILE="$SPRING_PROJECT_DIR/config/.env.$ENV"
ROOT_ENV_FILE="$PROJECT_ROOT/.env"

# Validate config file exists
if [[ ! -f "$ENV_CONFIG_FILE" ]]; then
    echo "Configuration file not found: $ENV_CONFIG_FILE"
    echo "Available environments:"
    ls -1 "$SPRING_PROJECT_DIR/config/" 2>/dev/null | grep "\.env\." | sed 's/\.env\./  - /'
    exit 1
fi

# Load environment variables
set -o allexport
[[ -f "$ROOT_ENV_FILE" ]] && source "$ROOT_ENV_FILE"
source "$ENV_CONFIG_FILE"
set +o allexport

# Set essential defaults
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-$ENV}"
export APP_PORT="${APP_PORT:-8080}"
export POSTGRES_USER="${POSTGRES_USER:-postgres}"
export ENV="$ENV"

cd "$PROJECT_ROOT"

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

# Execute action
case "$ACTION" in
    start)
        echo "🚀 Starting $ENV environment..."
        
        # Pass ENV to Docker Compose at runtime
        docker compose --env-file ./spring-publisher-service/config/.env.$ENV up -d --build

        # Wait for DB
        wait_for_service "db-publisher-service" \
            "docker compose exec -T db-publisher-service pg_isready -U '$POSTGRES_USER'"

        # Wait for Spring Boot
        wait_for_service "spring-publisher-service" \
            "curl -s -f http://localhost:${APP_PORT}/actuator/health"

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
        docker compose build --build-arg ENV=$ENV spring-publisher-service
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
        echo "Unknown action: $ACTION"
        exit 1
        ;;
esac