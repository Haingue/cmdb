# Configuration Management Database (CMDB)

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![CI Status](https://github.com/Haingue/cmdb/actions/workflows/ci.yml/badge.svg)](https://github.com/Haingue/cmdb/actions)
[![Contributors](https://img.shields.io/github/contributors/Haingue/cmdb)](https://github.com/Haingue/cmdb/graphs/contributors)

A platform to map an enterprise's application environment with a modular and scalable architecture.

### Goals

- Centralize all information about current infrastructure
- Centralize all information about current projects
- Create the link between project definitions and the current inventory

## Quick Start

### Prerequisites
- Java 17+
- Docker + Docker Compose
- Node.js (for frontend)

### Run Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/Haingue/cmdb.git
   cd cmdb
   ```

2. Start the services using Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Access the frontend:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## Monorepo Structure

| Directory | Purpose |
|-----------|---------|
| `adr/` | Architecture Decision Records |
| `bff/` | Backend For Frontend (API Gateway) |
| `core/` | Domain Logic (Hexagonal Architecture) |
| `frontend/` | Frontend Application (React) |
| `services/` | Business Services (e.g., Inventory Service) |
| `services/inventory/` | Inventory Management Service |
| `agregators/` | Data Ingestion Tools (GitHub Analyzer, Syslog Server) |
| `agregators/github-analyzer/` | GitHub repository analyzer |
| `agregators/syslog-server/` | Syslog collection and processing |
| `docs/` | Documentation (Astro + Starlight) |

## Architecture Overview

The CMDB follows a clean, modular architecture with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────────┐
│                           Frontend (React)                         │
└─────────────────────────────────────────────────────────────────┘
                                      ↓
┌─────────────────────────────────────────────────────────────────┐
│                    BFF - Backend For Frontend                     │
│                    (API Gateway & Aggregator)                      │
└─────────────────────────────────────────────────────────────────┘
                              ↓        ↓
              ┌───────────────────────┐ ┌─────────────────────┐
              │      Inventory        │ │   Data Aggregators   │
              │      Service          │ │  (GitHub, Syslog)    │
              └───────────────────────┘ └─────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                         Core Domain                                 │
│              (Shared Domain Logic - Hexagonal Architecture)        │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      PostgreSQL Database                           │
└─────────────────────────────────────────────────────────────────┘
```

## Key Features

### Inventory Service (`services/inventory/`)
- Entity management (servers, applications, databases, etc.)
- Relationship mapping between components
- Event tracking and audit logging
- Version and lifecycle management

### Data Aggregators (`agregators/`)
- **GitHub Analyzer**: Collects repository data, dependencies, and code metrics
- **Syslog Server**: Centralized log collection and processing

### BFF Layer (`bff/`)
- API Gateway for frontend applications
- Aggregates data from multiple services
- Provides GraphQL/REST endpoints

### Core Domain (`core/`)
- Shared domain models and business logic
- Implements Hexagonal Architecture (Ports & Adapters)
- Technology-agnostic interfaces

## Documentation

Full documentation is available in the [`docs/`](./docs/) directory, built with Astro and Starlight.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
