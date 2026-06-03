# ADR-001: Monorepo Structure

## Status

Accepted

## Context

The CMDB project consists of multiple interdependent components:
- Frontend application
- Backend For Frontend (BFF) API Gateway
- Core domain logic
- Multiple business services (Inventory, etc.)
- Data aggregation tools (GitHub analyzer, Syslog server)

We need a structure that:
- Allows independent development and deployment of services
- Maintains clear dependencies between components
- Facilitates code reuse (especially domain models)
- Simplifies onboarding for new developers

## Decision

We use a **monorepo** approach with the following structure:

```
cmdb/
├── adr/                    # Architecture Decision Records
├── bff/                    # Backend For Frontend (API Gateway)
├── core/                   # Domain Logic (Hexagonal Architecture)
├── frontend/               # Frontend Application (React)
├── services/               # Business Services
│   └── inventory/          # Inventory Management Service
├── agregators/             # Data Ingestion Tools
│   ├── github-analyzer/    # GitHub repository analyzer
│   └── syslog-server/      # Syslog collection and processing
└── docs/                   # Documentation (Astro + Starlight)
```

### Directory Purposes

| Directory | Purpose | Technologies |
|-----------|---------|--------------|
| `bff/` | API Gateway, aggregates backend services | Spring Boot, Java 17+ |
| `core/` | Shared domain models and business logic | Java, Hexagonal Architecture |
| `frontend/` | User interface for the CMDB | React, TypeScript |
| `services/` | Business services with their own databases | Spring Boot, Java 17+ |
| `services/inventory/` | Manages infrastructure and application inventory | Spring Boot, PostgreSQL |
| `agregators/` | External data collection and processing | Spring Boot, various APIs |
| `docs/` | Project documentation site | Astro, Starlight |

## Consequences

### Positive
- **Code reuse**: Core domain logic is shared across all services
- **Consistent development**: Single repository for all components
- **Simplified dependency management**: Easy to link components locally
- **Unified CI/CD**: All components can be built and tested together
- **Better visibility**: All project code in one place

### Negative
- **Repository size**: Larger repository as the project grows
- **Build time**: Longer build times when all components must be built
- **Permission complexity**: Need to manage permissions for different team members
- **Tooling requirement**: Need tools that support monorepos well

## Alternatives Considered

### 1. Polyrepo (Separate Repositories)
- **Rejected**: Would make code reuse more difficult, especially for core domain logic
- **Rejected**: Harder to maintain consistency across services
- **Rejected**: More complex CI/CD coordination

### 2. Hybrid Approach (Core in separate repo)
- **Rejected**: Would add complexity with version management
- **Rejected**: Dependencies between repositories can be fragile
- **Rejected**: Slower development cycle due to cross-repo dependencies

### 3. Package-based Monorepo (using npm/yarn/pnpm workspaces)
- **Rejected**: Java doesn't have native support for workspace-like dependency management
- **Considered**: Could be used for frontend + docs, but not for Java components
