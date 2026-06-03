# ADR-003: Service Organization

## Status

Accepted

## Context

The CMDB needs to provide various capabilities:
- Inventory management (servers, applications, databases, etc.)
- Cartography/visualization of infrastructure
- Data ingestion from external sources (GitHub, Syslog, etc.)
- API endpoints for frontend consumption

We need to decide how to organize these capabilities into services:
- Should we have one large service?
- Should we have multiple microservices?
- How do they communicate?

## Decision

We adopt a **modular monolith** approach with clear service boundaries:

```
┌─────────────────────────────────────────────────────────────────┐
│                         Monorepo Boundary                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────────┐  │
│  │   Inventory   │    │    BFF       │    │   Aggregators     │  │
│  │   Service     │    │              │    │                  │  │
│  │              │←───▶│ API Gateway  │◀───▶│  - GitHub        │  │
│  │ services/     │    │ bff/         │    │    Analyzer      │  │
│  │ inventory/    │    │              │    │  - Syslog        │  │
│  └──────────────┘    └──────────────┘    │    Server         │  │
│           │                                    └──────────────────┘  │
│           │                                                   │      │
│           ▼                                                   ▼      │
│  ┌──────────────┐    ┌──────────────────────────────────────────┐  │
│  │   Core        │    │              Database Cluster               │  │
│  │   Domain      │    │  ┌──────────────┐  ┌──────────────────┐  │  │
│  │   core/       │    │  │ PostgreSQL   │  │    Redis Cache   │  │  │
│  └──────────────┘    │  │ (Inventory)  │  │                  │  │  │
│         │            │  └──────────────┘  └──────────────────┘  │  │
│         └────────────▶│                                             │  │
│                        └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Service Breakdown

#### 1. Inventory Service (`services/inventory/`)
- **Responsibility**: CRUD operations for all CMDB entities
- **Domain**: Entities, Components, Projects, Environments, Events
- **Storage**: PostgreSQL database
- **API**: REST endpoints exposed internally

#### 2. BFF - Backend For Frontend (`bff/`)
- **Responsibility**: Aggregates data for frontend consumption
- **Pattern**: API Gateway + Aggregator
- **Clients**: Frontend application
- **API**: GraphQL and/or REST endpoints for UI

#### 3. Aggregators (`agregators/`)
- **Responsibility**: Collect and process data from external sources
- **Sub-services**:
  - `agregators/github-analyzer/`: GitHub repository analysis
  - `agregators/syslog-server/`: Syslog message collection and processing

### Communication Patterns

1. **Synchronous (REST/HTTP)**: Used for request-response between BFF and services
2. **Event-driven (Optional)**: For asynchronous notifications (future consideration)

### Data Flow

```
User → Frontend → BFF → Inventory Service → PostgreSQL
                     ↓
              Aggregators → External Sources
```

## Consequences

### Positive
- **Clear boundaries**: Each service has a well-defined responsibility
- **Independent development**: Services can be developed in parallel
- **Scalability**: Services can be scaled independently
- **Shared domain**: Core domain logic is reused across services
- **Simpler deployment**: Single artifact deployment (for now)

### Negative
- **Inter-service communication**: Need to manage communication between services
- **Transaction management**: Distributed transactions are complex
- **Deployment complexity**: As we grow, may need to split into true microservices

## Alternatives Considered

### 1. Single Monolithic Service
- **Rejected**: Too large, violates Single Responsibility Principle
- **Rejected**: Harder to scale individual components
- **Rejected**: Longer build and deployment times

### 2. Pure Microservices
- **Rejected**: Overkill for current scale
- **Rejected**: Adds complexity (service discovery, configuration, networking)
- **Rejected**: Harder to develop and test locally
- **Note**: May evolve to this if the project grows significantly

### 3. Microservices with Shared Database
- **Rejected**: Violates database isolation principle
- **Rejected**: Can lead to tight coupling through database schema
