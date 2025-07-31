#!/usr/bin/env bash

set -e

# Default values
ENV=${1:-dev}
ACTION=${2:-start}

# Paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
ANGULAR_PROJECT_DIR="$PROJECT_ROOT/angular-publisher-service"

# Validate Angular environment file exists
ANGULAR_ENV_FILE="$ANGULAR_PROJECT_DIR/src/environments/environment.$ENV.ts"
if [[ ! -f "$ANGULAR_ENV_FILE" ]]; then
    echo "❌ Missing Angular environment file: $ANGULAR_ENV_FILE"
    echo "Available environments:"
    ls -1 "$ANGULAR_PROJECT_DIR/src/environments/" | grep "environment\." | sed 's/environment\./  - /' | sed 's/\.ts$//'
    exit 1
fi

# Simple environment validation
case "$ENV" in
    dev|staging|prod) ;;
    *) echo "❌ Invalid environment: $ENV. Use dev, staging, or prod" && exit 1 ;;
esac

# Minimal Docker configuration - let Angular handle the environment specifics
export NG_ENV="$ENV"
export BUILD_VERSION="${BUILD_VERSION:-latest}"

# Determine Docker target based on environment
case "$ENV" in
    dev)
        export DOCKER_TARGET="development"
        export NODE_ENV="development"
        ;;
    staging|prod)
        export DOCKER_TARGET="production" 
        export NODE_ENV="production"
        ;;
esac

cd "$PROJECT_ROOT"

# Validation checks
if [[ ! -f "$ANGULAR_PROJECT_DIR/angular.json" ]]; then
    echo "❌ Angular project not found in: $ANGULAR_PROJECT_DIR" && exit 1
fi

if [[ ! -f "$PROJECT_ROOT/docker-compose.yml" ]]; then
    echo "❌ docker-compose.yml not found in: $PROJECT_ROOT" && exit 1
fi

# Verify service exists
SERVICE="angular-publisher-service"
if ! docker compose config --services 2>/dev/null | grep -q "^$SERVICE$"; then
    echo "❌ Service '$SERVICE' not found in docker-compose.yml"
    echo "Available services:"
    docker compose config --services 2>/dev/null | sed 's/^/  - /'
    exit 1
fi

# Actions
case "$ACTION" in
    start)
        echo "🚀 Starting Angular app in $ENV environment..."
        echo "📁 Using environment file: $ANGULAR_ENV_FILE"
        echo "🎯 Docker target: $DOCKER_TARGET"
        
        # Clean up
        docker compose down --remove-orphans 2>/dev/null || true
        
        # Build and start
        docker compose build "$SERVICE"
        docker compose up -d "$SERVICE"
        
        sleep 3
        docker compose ps "$SERVICE"
        
        # Get the actual port from docker-compose
        PORT=$(docker compose port "$SERVICE" 80 2>/dev/null | cut -d: -f2 || echo "4200")
        echo "🌐 Angular app: http://localhost:${PORT}"
        ;;

    stop)
        echo "⏹️  Stopping containers..."
        docker compose down --remove-orphans
        ;;

    restart)
        "$0" "$ENV" stop
        sleep 2
        "$0" "$ENV" start
        ;;

    logs)
        echo "📋 Logs for $SERVICE..."
        docker compose logs -f "$SERVICE"
        ;;

    build)
        echo "🔨 Building image for $SERVICE..."
        docker compose build "$SERVICE"
        ;;

    clean)
        echo "🧹 Cleaning up Docker..."
        docker compose down --remove-orphans --volumes
        docker system prune -f
        ;;

    shell)
        echo "💻 Opening shell in $SERVICE container..."
        docker compose exec "$SERVICE" /bin/bash || docker compose exec "$SERVICE" /bin/sh
        ;;

    test)
        echo "🧪 Running Angular tests..."
        docker compose exec "$SERVICE" npm test
        ;;

    *)
        echo "Usage: $0 [dev|staging|prod] [start|stop|logs|restart|build|clean|shell|test]"
        echo ""
        echo "Environments:"
        echo "  dev      - Development with hot reload"
        echo "  staging  - Production build for staging"
        echo "  prod     - Production build for production"
        ;;
esac