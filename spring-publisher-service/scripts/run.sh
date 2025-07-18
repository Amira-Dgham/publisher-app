#!/bin/bash

set -e

# Default values
ENV=${1:-dev}
ACTION=${2:-start}

# Paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_FILE="./config/.env.$ENV"
ROOT_ENV_FILE="./.env"  # Optional root .env (shared or fallback)

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Logging functions
log() { echo -e "${GREEN}[INFO]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }

# Validate env file exists
if [[ ! -f "$CONFIG_FILE" ]]; then
    error "Configuration file not found: $CONFIG_FILE"
fi

# Load environment variables
log "🔧 Loading environment config: $CONFIG_FILE"
set -o allexport
[[ -f "$ROOT_ENV_FILE" ]] && source "$ROOT_ENV_FILE"
source "$CONFIG_FILE"
set +o allexport

# Export required vars
export ENV="$ENV"
export BUILD_VERSION="${BUILD_VERSION:-latest}"

# Ensure external Docker network exists
if ! docker network inspect "$DOCKER_NETWORK" >/dev/null 2>&1; then
    log "🌐 Creating Docker network: $DOCKER_NETWORK"
    docker network create "$DOCKER_NETWORK"
else
    log "🌐 Docker network already exists: $DOCKER_NETWORK"
fi

# Move to script directory
cd "$SCRIPT_DIR"

# Execute actions
case "$ACTION" in
  start)
      log "🚀 Starting $ENV environment..."

      PROFILE=""
      if [[ "$ENV" == "staging" || "$ENV" == "prod" ]]; then
          # Check if docker compose supports --profile flag
          if docker compose up --help | grep -q -- '--profile'; then
              PROFILE="--profile monitoring"
              log "Using Docker Compose profile: monitoring"
          else
              warn "--profile not supported by your docker compose version, skipping"
          fi
      fi

      docker compose down --remove-orphans 2>/dev/null || true
      docker compose up -d --build $PROFILE

      docker compose ps
      echo ""
      log "🔗 Application URL: http://localhost:${APP_PORT:-8080}"
      ;;
    stop)
        log "🛑 Stopping $ENV environment..."
        docker compose down --remove-orphans
        log "✅ Stopped successfully!"
        ;;

    logs)
        log "📋 Showing logs for $ENV environment..."
        docker compose logs -f app
        ;;

    restart)
        log "🔄 Restarting $ENV environment..."
        docker compose down --remove-orphans
        sleep 2
        "$0" "$ENV" start
        ;;

    *)
        echo "Usage: $0 [dev|staging|prod] [start|stop|logs|restart]"
        echo ""
        echo "Examples:"
        echo "  $0 dev start      # Start development environment"
        echo "  $0 prod logs      # View production logs"
        exit 1
        ;;
esac