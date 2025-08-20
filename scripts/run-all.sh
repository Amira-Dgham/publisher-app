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
RUN_E2E_SCRIPT="$SCRIPT_DIR/run-e2e.sh"

# Validate scripts exist
for script in "$RUN_ANGULAR_SCRIPT" "$RUN_SPRING_SCRIPT" "$RUN_E2E_SCRIPT"; do
    if [[ ! -f "$script" ]]; then
        echo "Script not found: $script" && exit 1
    fi
done

# Make scripts executable
chmod +x "$RUN_ANGULAR_SCRIPT"
chmod +x "$RUN_SPRING_SCRIPT" 
chmod +x "$RUN_E2E_SCRIPT"

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
      # Start Spring Boot first (includes database)
      echo "🚀 Starting Spring Boot service in $ENV..."
      "$RUN_SPRING_SCRIPT" "$ENV" start
      
      # Wait for Spring Boot to be ready
      wait_for_service "Spring Boot" "http://localhost:8080/actuator/health" 180
      
      # Start Angular service
      echo "🚀 Starting Angular service in $ENV..."
      "$RUN_ANGULAR_SCRIPT" "$ENV" start
      
      # Wait for Angular to be ready
      wait_for_service "Angular" "http://localhost:4200" 120
      
      # Show final status
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
      "$RUN_E2E_SCRIPT" "$ENV" stop 2>/dev/null || true
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
      echo "📋 Showing logs for all services..."
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
              "$RUN_E2E_SCRIPT" "$ENV" logs
              ;;
          *)
              echo "Showing all logs..."
              docker compose logs -f
              ;;
      esac
      ;;

  status)
      echo "📊 Service Status:"
      docker compose ps
      echo ""
      "$RUN_E2E_SCRIPT" "$ENV" status 2>/dev/null || echo "E2E: Not running"
      ;;

  build)
      echo "🏗️ Building all services..."
      "$RUN_SPRING_SCRIPT" "$ENV" build
      "$RUN_ANGULAR_SCRIPT" "$ENV" build
      "$RUN_E2E_SCRIPT" "$ENV" build
      echo "✅ All services built!"
      ;;

  clean)
      echo "🧹 Cleaning up all resources..."
      "$RUN_E2E_SCRIPT" "$ENV" clean 2>/dev/null || true
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
          e2e)
              "$RUN_E2E_SCRIPT" "$ENV" shell
              ;;
          *)
              echo "❌ Invalid service: $service. Use angular, spring, db, or e2e"
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
              "$RUN_E2E_SCRIPT" "$ENV" test
              ;;
          all|"")
              echo "Running all tests (Spring + Angular + E2E)..."
              echo ""
              echo "🧪 Running Spring Boot tests..."
              "$RUN_SPRING_SCRIPT" "$ENV" test
              echo ""
              echo "🧪 Running Angular tests..."
              "$RUN_ANGULAR_SCRIPT" "$ENV" test
              echo ""
              echo "🧪 Running E2E tests..."
              "$RUN_E2E_SCRIPT" "$ENV" test
              ;;
          *)
              echo "❌ Invalid test target: $3. Use angular, spring, e2e, or all"
              exit 1
              ;;
      esac
      ;;

  e2e)
      # Direct E2E commands
      case "$3" in
          test|"")
              "$RUN_E2E_SCRIPT" "$ENV" test
              ;;
          start)
              "$RUN_E2E_SCRIPT" "$ENV" start
              ;;
          stop)
              "$RUN_E2E_SCRIPT" "$ENV" stop
              ;;
          shell)
              "$RUN_E2E_SCRIPT" "$ENV" shell
              ;;
          debug)
              "$RUN_E2E_SCRIPT" "$ENV" debug
              ;;
          *)
              "$RUN_E2E_SCRIPT" "$ENV" "$3"
              ;;
      esac
      ;;

  *)
      echo "❌ Invalid action: $ACTION"
      echo ""
      echo "Available actions:"
      echo "  start              - Start all services (Spring + Angular)"
      echo "  stop               - Stop all services"
      echo "  restart            - Restart all services"
      echo "  status             - Show service status"
      echo "  build              - Build all services"
      echo "  clean              - Clean all resources"
      echo "  logs [service]     - Show logs (angular|spring|db|e2e|all)"
      echo "  shell [service]    - Open shell (angular|spring|db|e2e)"
      echo "  test [target]      - Run tests (angular|spring|e2e|all)"
      echo "  e2e [action]       - E2E operations (test|start|stop|shell|debug)"
      echo ""
      echo "Usage: $0 [dev|staging|prod] [action] [options]"
      echo ""
      echo "Examples:"
      echo "  $0 dev start                    # Start all services"
      echo "  $0 dev test e2e                 # Run E2E tests only"
      echo "  $0 dev test all                 # Run all tests"
      echo "  $0 dev shell e2e                # Open E2E container shell"
      echo "  $0 dev e2e debug                # Debug E2E tests"
      echo "  $0 dev logs e2e                 # Show E2E logs"
      exit 1
      ;;
esac