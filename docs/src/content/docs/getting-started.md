---
title: Getting Started
description: Set up and run the CMDB project locally
---

# Getting Started with CMDB

This guide will help you set up the CMDB project on your local machine for development and testing purposes.

## Prerequisites

Before you begin, ensure you have the following installed:

| Requirement | Version | Verification Command |
|-------------|---------|---------------------|
| Java | 17 or higher | `java -version` |
| Docker | Latest | `docker --version` |
| Docker Compose | Latest | `docker-compose --version` |
| Node.js | 18 or higher | `node --version` |
| npm | 9 or higher | `npm --version` |

### Additional Tools (Optional)

- **Git**: For version control
- **Maven**: For Java project management (included in most Java installations)
- **PostgreSQL client**: For database inspection (e.g., pgAdmin, DBeaver)

## Quick Start

### 1. Clone the Repository

```bash
 git clone https://github.com/Haingue/cmdb.git
 cd cmdb
```

### 2. Start the Infrastructure

The project uses Docker Compose to manage its infrastructure dependencies:

```bash
 docker-compose up -d
```

This will start:
- PostgreSQL database for the Inventory service
- Any other required infrastructure

Wait for all containers to be healthy before proceeding:

```bash
 docker-compose ps
```

### 3. Build the Java Components

Build all the Java services and core:

```bash
 # From the root directory
 mvn clean install -DskipTests
```

Or build individual components:

```bash
 # Build core domain
 cd core && mvn clean install -DskipTests
 cd ..

 # Build inventory service
 cd services/inventory && mvn clean install -DskipTests
 cd ../..

 # Build BFF
 cd bff && mvn clean install -DskipTests
 cd ..

 # Build aggregators
 cd agregators/github-analyzer && mvn clean install -DskipTests
 cd ../syslog-server && mvn clean install -DskipTests
 cd ../..
```

### 4. Start the Services

Run the services using Maven Spring Boot plugin:

```bash
 # In separate terminals, run each service:

 # Inventory Service
 cd services/inventory
 mvn spring-boot:run

 # BFF
 cd bff
 mvn spring-boot:run

 # Aggregators (optional)
 cd agregators/github-analyzer
 mvn spring-boot:run

 cd agregators/syslog-server
 mvn spring-boot:run
```

### 5. Start the Frontend

```bash
 cd frontend
 npm install
 npm run dev
```

The frontend will be available at `http://localhost:3000` (or the port shown in the console).

## Development Workflow

### Running Tests

Run all tests:

```bash
 mvn test
```

Run tests for a specific module:

```bash
 cd core && mvn test
```

### Hot Reloading

- **Frontend**: Changes are automatically reloaded during development (`npm run dev`)
- **Backend**: Use Spring Boot DevTools for hot reloading:
  ```bash
  mvn spring-boot:run -Dspring-boot.run.profiles=dev
  ```

### Accessing Services

Once all services are running:

| Service | URL | Port |
|---------|-----|------|
| Frontend | http://localhost:3000 | 3000 |
| BFF API | http://localhost:8080 | 8080 |
| Inventory Service | http://localhost:8081 | 8081 |
| GitHub Analyzer | http://localhost:8082 | 8082 |
| Syslog Server | http://localhost:8083 | 8083 |
| PostgreSQL | localhost | 5432 |

## Troubleshooting

### Common Issues

#### Docker Containers Not Starting

```bash
# Check logs
docker-compose logs

# Restart containers
docker-compose down
docker-compose up -d
```

#### Java Build Failures

```bash
# Clean and rebuild
mvn clean install

# Check specific module
cd <module>
mvn clean compile
```

#### Frontend Dependencies

```bash
# Clean node_modules and reinstall
cd frontend
rm -rf node_modules package-lock.json
npm install
```

#### Port Conflicts

If a port is already in use:

```bash
# Find the process using the port (Linux/Mac)
lsof -i :<PORT>

# Kill the process
kill -9 <PID>

# Or use a different port
# Update application.properties for each service
```

## Next Steps

- [Learn about the Architecture](/architecture/)
- [Explore the API Reference](/api/)
- [Contribute to the Project](/contributing/)
