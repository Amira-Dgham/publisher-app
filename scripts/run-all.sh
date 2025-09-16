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
RUN_UI_SCRIPT="$SCRIPT_DIR/run-ui-tests.sh"
RUN_API_SCRIPT="$SCRIPT_DIR/run-api-tests.sh"

# Validate scripts exist
for script in "$RUN_ANGULAR_SCRIPT" "$RUN_SPRING_SCRIPT" "$RUN_UI_SCRIPT" "$RUN_API_SCRIPT"; do
    if [[ ! -f "$script" ]]; then
        echo "Script not found: $script" && exit 1
    fi
done

# Make scripts executable
chmod +x "$RUN_ANGULAR_SCRIPT" "$RUN_SPRING_SCRIPT" "$RUN_UI_SCRIPT" "$RUN_API_SCRIPT"

# Helper function to wait for service health
wait_for_service() {
    local service_name=$1
    local url=$2
    local timeout=${3:-120}
    local counter=0

    echo "⏳ Waiting for $service_name to be ready at $url..."

    while ! curl -s -f "$url" >/dev/null 2>&1; do
        if [ $counter -ge $timeout ]; then
            echo "❌ Timeout waiting for $service_name to be ready" && exit 1
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
      echo "🚀 Starting Spring Boot service in $ENV..."
      "$RUN_SPRING_SCRIPT" "$ENV" start
      wait_for_service "Spring Boot" "http://localhost:8080/actuator/health" 180

      echo "🚀 Starting Angular service in $ENV..."
      "$RUN_ANGULAR_SCRIPT" "$ENV" start
      wait_for_service "Angular" "http://localhost:4200" 120

      docker compose ps
      echo ""
      echo "🎉 All services are running!"
      echo "   Spring Boot: http://localhost:8080"
      echo "   Angular:     http://localhost:4200"
      echo ""
      echo "To run E2E tests: $0 $ENV test e2e"
      ;;

  stop)
      echo "🛑 Stopping all services..."
      "$RUN_UI_SCRIPT" "$ENV" stop 2>/dev/null || true
      "$RUN_API_SCRIPT" "$ENV" stop 2>/dev/null || true
      "$RUN_ANGULAR_SCRIPT" "$ENV" stop
      "$RUN_SPRING_SCRIPT" "$ENV" stop
      echo "✅ All services stopped!"
      ;;

  restart)
      echo "♻️  Restarting full application stack..."
      "$0" "$ENV" stop
      sleep 3
      "$0" "$ENV" start
      ;;

  logs)
      echo "📋 Showing logs for services..."
      case "$3" in
          angular)
              "$RUN_ANGULAR_SCRIPT" "$ENV" logs
              ;;
          spring)
              "$RUN_SPRING_SCRIPT" "$ENV" logs
              ;;
          db|database)
              "$RUN_SPRING_SCRIPT" "$ENV" logs db
              ;;
          e2e)
              "$RUN_UI_SCRIPT" "$ENV" logs
              "$RUN_API_SCRIPT" "$ENV" logs
              ;;
          *)
              docker compose logs -f
              ;;
      esac
      ;;

  status)
      echo "📊 Service Status:"
      docker compose ps
      echo ""
      "$RUN_UI_SCRIPT" "$ENV" status 2>/dev/null || echo "E2E UI: Not running"
      "$RUN_API_SCRIPT" "$ENV" status 2>/dev/null || echo "E2E API: Not running"
      ;;

  build)
      echo "🏗️ Building all services..."
      "$RUN_SPRING_SCRIPT" "$ENV" build
      "$RUN_ANGULAR_SCRIPT" "$ENV" build
      "$RUN_UI_SCRIPT" "$ENV" build
      "$RUN_API_SCRIPT" "$ENV" build
      echo "✅ All services built!"
      ;;

  clean)
      echo "🧹 Cleaning all resources..."
      "$RUN_UI_SCRIPT" "$ENV" clean 2>/dev/null || true
      "$RUN_API_SCRIPT" "$ENV" clean 2>/dev/null || true
      "$RUN_ANGULAR_SCRIPT" "$ENV" clean
      "$RUN_SPRING_SCRIPT" "$ENV" clean
      echo "✅ Cleanup completed!"
      ;;

  shell)
      local service=${3:-spring}
      echo "🔧 Opening shell in $service container..."
      case "$service" in
          angular)
              "$RUN_ANGULAR_SCRIPT" "$ENV" shell
              ;;
          spring)
              "$RUN_SPRING_SCRIPT" "$ENV" shell
              ;;
          db|database)
              "$RUN_SPRING_SCRIPT" "$ENV" shell db
              ;;
          ui)
              "$RUN_UI_SCRIPT" "$ENV" shell
              ;;
          api)
              "$RUN_API_SCRIPT" "$ENV" shell
              ;;
          *)
              echo "❌ Invalid service: $service. Use angular, spring, db, ui, or api"
              exit 1
              ;;
      esac
      ;;

  test)
      echo "🧪 Running tests..."
      case "$3" in
          angular)
              "$RUN_ANGULAR_SCRIPT" "$ENV" test
              ;;
          spring)
              "$RUN_SPRING_SCRIPT" "$ENV" test
              ;;
          e2e)
              "$RUN_UI_SCRIPT" test "$ENV"
              "$RUN_API_SCRIPT" test "$ENV"
              ;;
          all|"")
              echo "Running all tests (Spring + Angular + E2E)..."
              echo ""
              echo "🧪 Spring Boot tests..."
              "$RUN_SPRING_SCRIPT" "$ENV" test
              echo ""
              echo "🧪 Angular tests..."
              "$RUN_ANGULAR_SCRIPT" "$ENV" test
              echo ""
              echo "🧪 E2E tests..."
              "$RUN_UI_SCRIPT" test "$ENV"
              "$RUN_API_SCRIPT" test "$ENV"
              ;;
          *)
              echo "❌ Invalid test target: $3. Use angular, spring, e2e, or all"
              exit 1
              ;;
      esac
      ;;

  e2e)
      case "$3" in
          test|"")
              "$RUN_UI_SCRIPT" test "$ENV"
              "$RUN_API_SCRIPT" test "$ENV"
              ;;
          start)
              "$RUN_UI_SCRIPT" "$ENV" start
              "$RUN_API_SCRIPT" "$ENV" start
              ;;
          stop)
              "$RUN_UI_SCRIPT" "$ENV" stop
              "$RUN_API_SCRIPT" "$ENV" stop
              ;;
          shell)
              "$RUN_UI_SCRIPT" "$ENV" shell
              "$RUN_API_SCRIPT" "$ENV" shell
              ;;
          debug)
              "$RUN_UI_SCRIPT" "$ENV" debug
              "$RUN_API_SCRIPT" "$ENV" debug
              ;;
          *)
              "$RUN_UI_SCRIPT" "$ENV" "$3"
              "$RUN_API_SCRIPT" "$ENV" "$3"
              ;;
      esac
      ;;

  *)
      echo "❌ Invalid action: $ACTION"
      exit 1
      ;;
esac