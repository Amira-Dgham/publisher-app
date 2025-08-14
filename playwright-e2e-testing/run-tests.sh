#!/bin/bash

# Author E2E Testing Runner Script
# This script provides easy commands to run the Author E2E tests

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Spring Boot app is running
check_spring_boot() {
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        print_success "Spring Boot application is running on localhost:8080"
        return 0
    else
        print_error "Spring Boot application is not running on localhost:8080"
        print_warning "Please start the Spring Boot application first"
        return 1
    fi
}

# Function to run tests
run_tests() {
    local environment=${1:-dev}
    print_status "Running Author E2E tests with environment: $environment"
    
    if ! check_spring_boot; then
        exit 1
    fi
    
    print_status "Starting test execution..."
    mvn clean test -Dtest.env=$environment
    
    if [ $? -eq 0 ]; then
        print_success "All tests passed successfully!"
    else
        print_error "Some tests failed. Check the output above for details."
        exit 1
    fi
}

# Function to run tests with Allure reporting
run_tests_with_reporting() {
    local environment=${1:-dev}
    print_status "Running Author E2E tests with Allure reporting (environment: $environment)"
    
    if ! check_spring_boot; then
        exit 1
    fi
    
    print_status "Starting test execution with Allure reporting..."
    mvn clean test allure:report -Dtest.env=$environment
    
    if [ $? -eq 0 ]; then
        print_success "All tests passed successfully!"
        print_status "Allure report generated. Open target/site/allure-maven-plugin/index.html in your browser"
    else
        print_error "Some tests failed. Check the output above for details."
        exit 1
    fi
}

# Function to show help
show_help() {
    echo "Author E2E Testing Runner"
    echo ""
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  test [env]     Run tests (default environment: dev)"
    echo "  report [env]   Run tests with Allure reporting (default environment: dev)"
    echo "  help           Show this help message"
    echo ""
    echo "Environments:"
    echo "  dev            Development environment (default)"
    echo "  staging        Staging environment"
    echo "  prod           Production environment"
    echo ""
    echo "Examples:"
    echo "  $0 test              # Run tests in dev environment"
    echo "  $0 test staging      # Run tests in staging environment"
    echo "  $0 report            # Run tests with reporting in dev environment"
    echo "  $0 report prod       # Run tests with reporting in prod environment"
}

# Main script logic
case "${1:-test}" in
    "test")
        run_tests "${2:-dev}"
        ;;
    "report")
        run_tests_with_reporting "${2:-dev}"
        ;;
    "help"|"-h"|"--help")
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac 