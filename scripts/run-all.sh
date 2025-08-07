#!/bin/bash

set -e

# Default values
ENV=${1:-dev}
ACTION=${2:-start}

# Paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
RUN_ANGULAR_SCRIPT="$SCRIPT_DIR/run-angular.sh"
RUN_SPRING_SCRIPT="$SCRIPT_DIR/run-spring.sh"

# Validate scripts exist
if [[ ! -f "$RUN_ANGULAR_SCRIPT" ]]; then
    echo "Angular script not found: $RUN_ANGULAR_SCRIPT" && exit 1
fi

if [[ ! -f "$RUN_SPRING_SCRIPT" ]]; then
    echo "Spring script not found: $RUN_SPRING_SCRIPT" && exit 1
fi

# Make scripts executable
chmod +x "$RUN_ANGULAR_SCRIPT"
chmod +x "$RUN_SPRING_SCRIPT"

# Helper function to wait for service health
wait_for_service() {
    local service_name=$1
    local url=$2
    local timeout=${3:-120}
    local counter=0
    
    echo "⏳ Waiting for $service_name to be ready at $url..."
    
    while ! curl -s -f "$url" >/dev/null 2>&1; do
        if [ $counter -ge $timeout ]; then
            echo "Timeout waiting for $service_name to be ready" && exit 1
        fi
        sleep 2
        counter=$((counter + 2))
        echo -n "."
    done
    echo ""
    echo "✅ $service_name is ready!"
}

# Execute actions
case "$ACTION" in
  start)      
      # Start Spring Boot first (includes database)
      echo "Starting Spring Boot service in $ENV ..."
      "$RUN_SPRING_SCRIPT" "$ENV" start
      
      # Wait for Spring Boot to be ready
      wait_for_service "Spring Boot" "http://localhost:8080/actuator/health" 180
      
      # Start Angular service
      echo "Starting Angular service in $ENV ..."
      "$RUN_ANGULAR_SCRIPT" "$ENV" start
      
      # Wait for Angular to be ready
      wait_for_service "Angular" "http://localhost:4200" 120
      
      # Show final status
      docker compose ps
      ;;

  stop)
      "$RUN_ANGULAR_SCRIPT" "$ENV" stop
      "$RUN_SPRING_SCRIPT" "$ENV" stop
      echo "All services stopped!"
      ;;

  restart)
      echo "Restarting full application stack..."
      "$0" "$ENV" stop
      sleep 3
      "$0" "$ENV" start
      ;;

  logs)
      echo "📋 Showing logs for all services..."
      if [[ "$3" == "angular" ]]; then
          "$RUN_ANGULAR_SCRIPT" "$ENV" logs
      elif [[ "$3" == "spring" ]]; then
          "$RUN_SPRING_SCRIPT" "$ENV" logs
      elif [[ "$3" == "db" ]]; then
          "$RUN_SPRING_SCRIPT" "$ENV" logs db
      else
          echo "Showing all logs..."
          docker compose logs -f
      fi
      ;;

  status)
      docker compose ps
      ;;

  build)
      echo "🏗️ Building all services..."
      "$RUN_SPRING_SCRIPT" "$ENV" build
      "$RUN_ANGULAR_SCRIPT" "$ENV" build
      echo "All services built!"
      ;;

  clean)
      echo "🧹 Cleaning up all resources..."
      "$RUN_ANGULAR_SCRIPT" "$ENV" clean
      "$RUN_SPRING_SCRIPT" "$ENV" clean
      echo "Cleanup completed!"
      ;;

  shell)
      local service=${3:-spring}
      echo "Opening shell in $service container..."
      case "$service" in
          angular)
              "$RUN_ANGULAR_SCRIPT" "$ENV" shell
              ;;
          spring)
              "$RUN_SPRING_SCRIPT" "$ENV" shell
              ;;
          db)
              "$RUN_SPRING_SCRIPT" "$ENV" shell
              ;;
          *)
              echo "Invalid service: $service. Use angular, spring, or db"
              exit 1
              ;;
      esac
      ;;

  test)
      echo "🧪 Running tests..."
      if [[ "$3" == "angular" ]]; then
          "$RUN_ANGULAR_SCRIPT" "$ENV" test
      elif [[ "$3" == "spring" ]]; then
          "$RUN_SPRING_SCRIPT" "$ENV" test
      else
          echo "Running all tests..."
          "$RUN_SPRING_SCRIPT" "$ENV" test
          "$RUN_ANGULAR_SCRIPT" "$ENV" test
      fi
      ;;

  *)
      exit 1
      ;;
esac