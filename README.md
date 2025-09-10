# Publisher Application

## Overview
A comprehensive publishing management system built with a microservices architecture, consisting of:
- Spring Boot backend service
- Angular frontend application
- End-to-end testing with Playwright
- Containerized deployment using Docker

## Project Architecture

```
├── Frontend Layer (Angular)
│   └── Angular Publisher Service
├── Backend Layer (Spring Boot)
│   └── Spring Publisher Service
├── Testing Layer
│   └── Playwright E2E Testing
└── Infrastructure Layer
    └── Docker Containers
```

![Architecture Diagram](docs/images/architecture.png)

## Project Structure

```
publisher-app/
├── angular-publisher-service/    # Frontend Angular application
├── spring-publisher-service/     # Backend Spring Boot service
├── playwright-e2e-testing/      # E2E testing suite
├── scripts/                     # Deployment and run scripts
│   ├── run-all.sh              # Run entire stack
│   ├── run-angular.sh          # Run frontend only
│   ├── run-spring.sh           # Run backend only
│   └── run-e2e.sh             # Run E2E tests
├── docker-compose.yml          # Docker composition
└── README.md                   # This file
```

## Technologies Used

### Frontend (Angular)
- Angular 17+
- TypeScript
- Docker container

### Backend (Spring Boot)
- Java 17
- Spring Boot 3.5.3
- PostgreSQL
- Docker container

### Testing
- Playwright
- Java
- TestNG
- Allure Reporting

### Infrastructure
- Docker
- Docker Compose
- Shell Scripts

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 17
- Node.js 18+
- Maven 3.6+

### Quick Start

1. Clone the repository:
```bash
git clone https://github.com/Amira-Dgham/publisher-app.git
cd publisher-app
```

2. Start the entire stack:
```bash
./scripts/run-all.sh
```

This will:
- Build and start all Docker containers
- Initialize the database
- Start the Spring Boot backend
- Launch the Angular frontend
- Setup the E2E testing environment

### Individual Component Setup

#### Backend Service
```bash
./scripts/run-spring.sh dev start
```

#### Frontend Service
```bash
./scripts/run-angular.sh dev start
```

#### E2E Tests
```bash
./scripts/run-e2e.sh
```

## Docker Configuration

### Docker Compose Overview
The application uses Docker Compose to manage multiple containers:

```yaml
services:
  frontend:
    build: ./angular-publisher-service
    ports:
      - "4200:80"
    depends_on:
      - backend

  backend:
    build: ./spring-publisher-service
    ports:
      - "8080:8080"
    depends_on:
      - db

  db:
    image: postgres:latest
    environment:
      POSTGRES_DB: publisher
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: secret

  e2e:
    build: ./playwright-e2e-testing
    depends_on:
      - frontend
      - backend
```

### Running with Docker

1. Build all containers:
```bash
docker-compose build
```

2. Start the stack:
```bash
docker-compose up -d
```

3. Check status:
```bash
docker-compose ps
```

4. View logs:
```bash
docker-compose logs -f
```

## Available Endpoints

### Frontend
- Main Application: http://localhost:4200

### Backend
- API Base URL: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs

## Testing

### E2E Tests
```bash
# Run all E2E tests
./scripts/run-e2e.sh

# Run specific test suite
./scripts/run-e2e.sh --suite=publishers
```

Test reports are available at:
- Allure Report: `playwright-e2e-testing/target/allure-results`

## Monitoring & Logging

### Application Monitoring
- Spring Actuator: http://localhost:8080/actuator
- Angular Performance: http://localhost:4200/metrics

### Logs
- Backend Logs: `spring-publisher-service/logs/`
- Frontend Logs: `angular-publisher-service/logs/`
- E2E Test Logs: `playwright-e2e-testing/logs/`

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Additional Documentation
- [Spring Service Documentation](spring-publisher-service/README.md)
- [Angular Service Documentation](angular-publisher-service/README.md)
- [E2E Testing Documentation](playwright-e2e-testing/README.md)

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
