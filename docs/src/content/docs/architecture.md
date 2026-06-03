---
title: Architecture Overview
description: Understand the CMDB project architecture and its components
---

# CMDB Architecture

The CMDB follows a modular, clean architecture designed for scalability and maintainability.

## High-Level Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                           User Interface                            │
│                         (Frontend - React)                         │
└─────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Backend For Frontend (BFF)                       │
│                    (API Gateway & Aggregator)                       │
│                                                                      │
│  - Aggregates data from multiple services                            │
│  - Provides GraphQL/REST endpoints                                   │
│  - Handles authentication and authorization                       │
│  - Rate limiting and request validation                            │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│   Inventory       │ │   Aggregators    │ │   Other Services │
│   Service         │ │                  │ │                  │
│                   │ │ - GitHub         │ │                  │
│ - Entity CRUD     │ │   Analyzer       │ │                  │
│ - Relationships   │ │ - Syslog         │ │                  │
│ - Events          │ │   Server         │ │                  │
│ - Versioning      │ │                  │ │                  │
└──────────────────┘ └──────────────────┘ └──────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Core Domain Layer                           │
│              (Hexagonal Architecture - Shared Library)             │
│                                                                      │
│  - Domain Entities (Entity, Component, Project, etc.)             │
│  - Domain Services                                                 │
│  - Ports (Interfaces) - Defines contracts with outside world      │
│  - No infrastructure dependencies                                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Data Storage Layer                             │
│                                                                      │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐  │
│  │   PostgreSQL     │  │   Redis Cache    │  │   File Storage   │  │
│  │   (Primary DB)   │  │   (Optional)     │  │   (Optional)      │  │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Monorepo Structure

The project is organized as a monorepo with the following directory structure:

```
cmdb/
├── adr/                    # Architecture Decision Records
│   ├── README.md           # ADR index and guidelines
│   ├── 001-monorepo-structure.md
│   ├── 002-hexagonal-architecture.md
│   ├── 003-service-organization.md
│   └── 004-technology-stack.md
│
├── bff/                    # Backend For Frontend
│   ├── src/                # Java/Spring Boot source
│   └── pom.xml             # Maven configuration
│
├── core/                   # Core Domain Logic
│   ├── src/                # Domain entities and services
│   └── pom.xml
│
├── frontend/               # Frontend Application
│   ├── src/                # React/TypeScript source
│   ├── public/             # Static assets
│   └── package.json        # npm configuration
│
├── services/               # Business Services
│   └── inventory/          # Inventory Service
│       ├── src/            # Spring Boot application
│       └── pom.xml
│
├── agregators/             # Data Ingestion Tools
│   ├── github-analyzer/    # GitHub repository analyzer
│   │   ├── src/
│   │   └── pom.xml
│   └── syslog-server/      # Syslog collection and processing
│       ├── src/
│       └── pom.xml
│
└── docs/                   # Documentation
    ├── src/
    │   └── content/
    │       └── docs/       # Documentation pages
    └── astro.config.mjs     # Astro configuration
```

## Component Details

### Core Domain (`core/`)

Implements Hexagonal Architecture with:

- **Domain Layer**: Contains business entities and value objects
  - `Entity`, `Component`, `Project`, `Environment`, `Event`, `Technology`, etc.
  - Rich domain models with business logic
  - Immutable value objects

- **Application Layer**: Contains use cases and application services
  - Orchestrates domain objects to fulfill use cases
  - Contains application-specific business rules

- **Ports**: Interfaces that define how the domain interacts with the outside world
  - **Inbound Ports**: Defines how external code can drive the domain
  - **Outbound Ports**: Defines how the domain can be driven by external code

The core module has **zero dependencies** on Spring, databases, or other infrastructure concerns.

### Inventory Service (`services/inventory/`)

Provides:

- **Entity Management**: CRUD operations for all CMDB entities
- **Relationship Management**: Define and query relationships between entities
- **Event Tracking**: Automatic audit logging of all changes
- **Version Management**: Track versions of technologies and components
- **Search**: Full-text and structured search capabilities

**Storage**: PostgreSQL database with JPA/Hibernate

**API**: RESTful endpoints for internal consumption (consumed by BFF)

### BFF - Backend For Frontend (`bff/`)

Acts as:

- **API Gateway**: Routes requests to appropriate services
- **Aggregator**: Combines data from multiple services
- **Transformer**: Adapts backend data to frontend needs
- **Security Layer**: Handles authentication and authorization

**API**: Provides both REST and GraphQL endpoints for the frontend

### Aggregators (`agregators/`)

#### GitHub Analyzer

Collects and processes:
- Repository metadata (name, description, stars, etc.)
- Commit history and statistics
- Branch and tag information
- Pull request and issue data
- Code metrics (lines of code, complexity, etc.)
- Dependency information

#### Syslog Server

Provides:
- Syslog message collection (UDP/TCP)
- Message parsing and normalization
- Event extraction and correlation
- Forwarding to appropriate services
- Log retention and archiving

### Frontend (`frontend/`)

React-based application that provides:

- **Dashboard**: Overview of the entire infrastructure
- **Inventory Browser**: Explore and filter entities
- **Project View**: View projects and their components
- **Relationship Graph**: Visualize connections between entities
- **Reports**: Generate and view various reports
- **Administration**: User and configuration management

## Technology Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| Core Domain | Java 17+ | Domain logic |
| Backend Services | Spring Boot 3.x | Web framework, DI, etc. |
| Database | PostgreSQL 15+ | Primary data storage |
| ORM | JPA/Hibernate 6.x | Object-relational mapping |
| Frontend | React 18+ | User interface |
| Frontend Language | TypeScript 5.x | Type safety |
| Documentation | Astro + Starlight | Documentation site |
| Containerization | Docker | Application packaging |
| Orchestration | Docker Compose | Multi-container management |

## Data Flow

### User Request Flow

```
1. User interacts with Frontend
2. Frontend makes API call to BFF
3. BFF authenticates and validates request
4. BFF determines which services to query
5. BFF calls Inventory Service and/or Aggregators
6. Services process request using Core Domain
7. Core Domain uses Ports to access infrastructure
8. Adapters implement Ports to access databases/APIs
9. Response flows back through the chain
10. Frontend renders the data
```

### Data Aggregation Flow

```
1. External source (GitHub/Syslog) sends data
2. Aggregator service receives and processes data
3. Aggregator transforms data to domain format
4. Aggregator calls Inventory Service to update entities
5. Inventory Service uses Core Domain to persist data
6. Events are recorded for audit trail
```

## Design Patterns

### Hexagonal Architecture (Ports & Adapters)

The core domain uses Hexagonal Architecture to maintain independence from infrastructure concerns:

```
┌─────────────────────────────────────────────┐
│                 Domain Layer                   │
│  ┌─────────────────────────────────────────┐  │
│  │              Application                  │  │
│  │  ┌──────────┐  ┌──────────┐  ┌────────┐  │  │
│  │  │ Use Case │  │ Use Case │  │ Service │  │  │
│  │  └────┬─────┘  └────┬─────┘  └────┬────┘  │  │
│  │       │             │             │        │  │
│  └───────┼─────────────┼─────────────┼────────┘  │
│          │             │             │           │
│  ┌───────▼─────────────▼─────────────▼────────┐  │
│  │                 Ports Interface             │  │
│  │  ┌──────────┐  ┌──────────┐  ┌───────────┐ │  │
│  │  │ Inbound │  │ Outbound │  │ Outbound  │ │  │
│  │  │  Port   │  │   Port   │  │   Port    │ │  │
│  │  └─────────┘  └─────────┘  └──────────┘ │  │
│  └───────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
        │                         │                 │
        ▼                         ▼                 ▼
┌──────────────┐       ┌──────────────┐      ┌──────────────┐
│  REST API    │       │ PostgreSQL   │      │ External API │
│  Adapter      │       │ Adapter      │      │ Adapter      │
└──────────────┘       └──────────────┘      └──────────────┘
```

### Repository Pattern

Used in the core domain for data access abstraction:

```java
// Port (Interface in core domain)
public interface EntityRepository {
    Entity findById(UUID id);
    Entity save(Entity entity);
    void delete(UUID id);
    // ...
}

// Adapter (Implementation in service module)
public class JpaEntityRepository implements EntityRepository {
    // JPA implementation
}
```

### Service Pattern

Each business service follows a consistent pattern:

```
Service Module
├── src/main/java
│   └── com/haingue/cmdb/service
│       ├── application  # Use cases
│       ├── domain       # Domain models (if service-specific)
│       ├── infrastructure # Adapters
│       └── web          # Controllers, DTOs
└── pom.xml
```

## See Also

- [Architecture Decision Records (ADRs)](/adrs/)
- [Technology Stack Details](/architecture/technology-stack/)
- [Service Organization ADR](/adrs/003-service-organization/)
- [Hexagonal Architecture ADR](/adrs/002-hexagonal-architecture/)
