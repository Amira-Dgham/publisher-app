#!/bin/bash
set -euo pipefail

ENV=${2:-dev}
ACTION=${1:-test}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
TEST_RESULTS_DIR="$PROJECT_ROOT/ui-automation-testing/target"

ALLURE_CONTAINER_NAME="allure-server-ui"
ALLURE_PORT=8082
ALLURE_RESULTS="$TEST_RESULTS_DIR/allure-results"

mkdir -p "$TEST_RESULTS_DIR"

info()    { echo -e "[INFO] $1"; }
success() { echo -e "[SUCCESS] $1"; }
error()   { echo -e "[ERROR] $1"; }

run_tests_in_container() {
    info "Running UI E2E container..."
    docker stop "$ALLURE_CONTAINER_NAME" >/dev/null 2>&1 || true

    docker compose run --rm \
        -e TEST_ENV="$ENV" \
        ui-automation-testing \
        bash -c "cd /e2e && mvn clean test -Dmaven.test.failure.ignore=true allure:report -P$ENV -Dtest.env=$ENV"

    success "UI Tests completed!"
    info "Allure report available in: $TEST_RESULTS_DIR/site/allure-maven-plugin/index.html"

    info "Starting Allure server at http://localhost:$ALLURE_PORT"
    docker run -d --rm \
        --name "$ALLURE_CONTAINER_NAME" \
        -p "$ALLURE_PORT":4040 \
        -v "$ALLURE_RESULTS":/app/allure-results \
        frankescobar/allure-docker-service
}

case "$ACTION" in
    test|report) run_tests_in_container ;;
    start) docker compose up -d ui-automation-testing ;;
    stop)  docker compose stop ui-automation-testing ;;
    logs)  docker compose logs -f ui-automation-testing ;;
    clean) docker compose down --remove-orphans --volumes ;;
    help|-h|--help)
        echo "Usage: $0 [COMMAND] [ENV]"
        echo "Commands: test|report, start, stop, logs, clean"
        echo "Environments: dev, staging, prod (default: dev)"
        ;;
    *) error "Unknown command: $ACTION"; exit 1 ;;
esac