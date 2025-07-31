#!/bin/bash

set -e

# Default values
ENV=${1:-dev}
ACTION=${2:-test}
TEST_SUITE=${3:-all}

# Paths - Fixed for root project structure
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"  # Script is now in project root
CONFIG_FILE="$PROJECT_ROOT/config/.env.$ENV"
ROOT_ENV_FILE="$PROJECT_ROOT/.env"  # Optional root .env (shared or fallback)
E2E_PROJECT_DIR="$PROJECT_ROOT/playwright-e2e-testing"



# Validate environment files exist
if [[ ! -f "$CONFIG_FILE" ]]; then
    echo "Configuration file not found: $CONFIG_FILE" && exit 1
fi

# Check if E2E project exists
if [[ ! -d "$E2E_PROJECT_DIR" ]]; then
    echo "E2E project directory not found: $E2E_PROJECT_DIR" && exit 1
fi

# Load environment variables
echo "🔧 Loading environment config: $CONFIG_FILE"
set -o allexport
[[ -f "$ROOT_ENV_FILE" ]] && source "$ROOT_ENV_FILE"
source "$CONFIG_FILE"
set +o allexport

# Export required vars
export ENV="$ENV"
export BUILD_VERSION="${BUILD_VERSION:-latest}"
export NODE_ENV="${NODE_ENV:-$ENV}"

# E2E specific exports
export E2E_ENV="$ENV"
export PLAYWRIGHT_BROWSERS_PATH="$E2E_PROJECT_DIR/.browsers"

# Test environment URLs
export FRONTEND_BASE_URL="http://localhost:${ANGULAR_PORT:-4200}"
export API_BASE_URL="http://localhost:${APP_PORT:-8080}"
export DB_URL="localhost:${POSTGRES_PORT:-5434}"

# Test configuration
export TEST_TIMEOUT="${TEST_TIMEOUT:-30000}"
export TEST_RETRIES="${TEST_RETRIES:-2}"
export HEADLESS="${HEADLESS:-true}"
export PARALLEL_WORKERS="${PARALLEL_WORKERS:-4}"

# Results and reports
export TEST_RESULTS_DIR="$PROJECT_ROOT/test-results"
export PLAYWRIGHT_REPORT_DIR="$PROJECT_ROOT/playwright-report"
export ALLURE_RESULTS_DIR="$PROJECT_ROOT/allure-results"

# Ensure external Docker network exists
if ! docker network inspect "$DOCKER_NETWORK" >/dev/null 2>&1; then
    echo "🌐 Creating Docker network: $DOCKER_NETWORK"
    docker network create "$DOCKER_NETWORK"
else
    echo "🌐 Docker network already exists: $DOCKER_NETWORK"
fi

# Move to project root directory
cd "$PROJECT_ROOT"

# Helper functions
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
    log "✅ $service_name is ready!"
}

ensure_services_running() {
    header "🔍 Checking if required services are running..."
    
    # Check if services are running
    if ! docker compose ps spring-publisher-service | grep -q "Up"; then
        echo "🍃 Starting Spring Boot service..."
        docker compose up -d db-publisher-service spring-publisher-service
        wait_for_service "Spring Boot" "$API_BASE_URL/actuator/health"
    else
        echo "✅ Spring Boot service is already running"
    fi
    
    if [[ "$ENV" == "prod" || "$ENV" == "staging" ]]; then
        if ! docker compose ps nginx-publisher-service | grep -q "Up"; then
            echo "🌐 Starting Nginx service..."
            docker compose --profile production up -d nginx-publisher-service
            wait_for_service "Nginx" "$FRONTEND_BASE_URL"
        else
            echo "✅ Nginx service is already running"
        fi
    else
        if ! docker compose ps angular-publisher-service | grep -q "Up"; then
            echo "🅰️  Starting Angular service..."
            docker compose --profile development up -d angular-publisher-service
            wait_for_service "Angular" "$FRONTEND_BASE_URL"
        else
            echo "✅ Angular service is already running"
        fi
    fi
}

# Execute actions
case "$ACTION" in
    test)
        echo "🧪 Running E2E tests in $ENV environment..."
        
        # Ensure all required services are running
        ensure_services_running
        
        # Create results directories
        mkdir -p "$TEST_RESULTS_DIR" "$PLAYWRIGHT_REPORT_DIR" "$ALLURE_RESULTS_DIR"
        
        # Build and run E2E tests
        echo "🏗️  Building E2E test container..."
        docker compose build playwright-e2e-testing
        
        # Run tests based on suite
        case "$TEST_SUITE" in
            all)
                echo "🚀 Running all E2E tests..."
                docker compose --profile testing run --rm playwright-e2e-testing npm run test
                ;;
            smoke)
                echo "💨 Running smoke tests..."
                docker compose --profile testing run --rm playwright-e2e-testing npm run test:smoke
                ;;
            regression)
                echo "🔄 Running regression tests..."
                docker compose --profile testing run --rm playwright-e2e-testing npm run test:regression
                ;;
            api)
                echo "🔌 Running API tests..."
                docker compose --profile testing run --rm playwright-e2e-testing npm run test:api
                ;;
            ui)
                echo "🖥️  Running UI tests..."
                docker compose --profile testing run --rm playwright-e2e-testing npm run test:ui
                ;;
            *)
                echo "🎯 Running custom test suite: $TEST_SUITE"
                docker compose --profile testing run --rm playwright-e2e-testing npm run test -- --grep "$TEST_SUITE"
                ;;
        esac
        
        # Show test results
        if [[ -f "$PLAYWRIGHT_REPORT_DIR/index.html" ]]; then
            echo "📊 Test report generated: $PLAYWRIGHT_REPORT_DIR/index.html"
            echo "💡 Open report: open $PLAYWRIGHT_REPORT_DIR/index.html"
        fi
        ;;
        
    debug)
        echo "🐛 Running E2E tests in debug mode..."
        ensure_services_running
        
        export HEADLESS=false
        export PLAYWRIGHT_DEBUG=1
        
        docker compose --profile testing run --rm \
            -e HEADLESS=false \
            -e PLAYWRIGHT_DEBUG=1 \
            playwright-e2e-testing npm run test:debug
        ;;
        
    interactive)
        echo "🎮 Running E2E tests in interactive mode..."
        ensure_services_running
        
        docker compose --profile testing run --rm \
            -e HEADLESS=false \
            playwright-e2e-testing npm run test:ui
        ;;
        
    install)
        echo "📦 Installing E2E test dependencies..."
        docker compose build playwright-e2e-testing
        docker compose --profile testing run --rm playwright-e2e-testing npm install
        docker compose --profile testing run --rm playwright-e2e-testing npx playwright install
        echo "✅ E2E dependencies installed!"
        ;;
        
    report)
        echo "📊 Generating test report..."
        if [[ -d "$ALLURE_RESULTS_DIR" && "$(ls -A $ALLURE_RESULTS_DIR)" ]]; then
            docker compose --profile testing run --rm playwright-e2e-testing npm run report:allure
        else
            docker compose --profile testing run --rm playwright-e2e-testing npm run report:html
        fi
        ;;
        
    clean)
        echo "🧹 Cleaning up E2E test resources..."
        docker compose --profile testing down --remove-orphans
        rm -rf "$TEST_RESULTS_DIR" "$PLAYWRIGHT_REPORT_DIR" "$ALLURE_RESULTS_DIR"
        docker system prune -f
        echo "✅ E2E cleanup completed!"
        ;;
        
    shell)
        echo "🐚 Opening shell in E2E container..."
        ensure_services_running
        docker compose --profile testing run --rm playwright-e2e-testing /bin/bash
        ;;
        
    logs)
        echo "📋 Showing E2E test logs..."
        docker compose logs -f playwright-e2e-testing
        ;;
        
    status)
        echo "📊 E2E Test Environment Status:"
        echo ""
        echo "🔗 Service URLs:"
        echo "   Frontend: $FRONTEND_BASE_URL"
        echo "   Backend:  $API_BASE_URL"
        echo "   Database: $DB_URL"
        echo ""
        echo "📁 Test Directories:"
        echo "   Results:  $TEST_RESULTS_DIR"
        echo "   Reports:  $PLAYWRIGHT_REPORT_DIR"
        echo "   Allure:   $ALLURE_RESULTS_DIR"
        echo ""
        docker compose ps
        ;;

    *)
        echo "Usage: $0 [env] [action] [test-suite]"
        echo ""
        echo "Environments:"
        echo "  dev, staging, prod                    (default: dev)"
        echo ""
        echo "Actions:"
        echo "  test        - Run E2E tests"
        echo "  debug       - Run tests in debug mode"
        echo "  interactive - Run tests interactively"
        echo "  install     - Install test dependencies"
        echo "  report      - Generate test reports"
        echo "  clean       - Clean up test resources"
        echo "  shell       - Open shell in test container"
        echo "  logs        - View test logs"
        echo "  status      - Show environment status"
        echo ""
        echo "Test Suites:"
        echo "  all         - All tests (default)"
        echo "  smoke       - Smoke tests"
        echo "  regression  - Regression tests"
        echo "  api         - API tests only"
        echo "  ui          - UI tests only"
        echo "  [custom]    - Custom test pattern"
        echo ""
        echo "Examples:"
        echo "  $0 dev test all           # Run all tests in dev"
        echo "  $0 prod test smoke        # Run smoke tests in prod"
        echo "  $0 staging debug          # Debug tests in staging"
        echo "  $0 dev test \"login\"       # Run tests matching 'login'"
        echo "  $0 dev interactive        # Interactive test mode"
        exit 1
        ;;
esac