---
title: CORE Domain
description: CMDB CORE module documentation - Domain models, business logic, and validation rules
---

# CORE Domain Module

The CORE module contains the domain models, business logic, and validation rules for the CMDB. This is the heart of the application, implementing all business rules and workflows following Domain-Driven Design principles.

## Documentation

### Core Concepts
- **[Domain Business Logic](./core-domain-logic.md)** - Complete overview of all business logic, validation chains, event management, and domain rules

### Detailed Specifications
- **[Validation Chains](./core-validation-chains.md)** - Detailed validation workflows for each entity type
- **[Event Management](./core-event-management.md)** - Comprehensive guide to event tracking, audit trails, and state changes
- **[Business Rules](./core-business-rules.md)** - Complete catalog of all domain-specific business rules and constraints

## Module Structure

```
core/
├── src/main/java/com/management/cmdb/core/
│   ├── models/
│   │   ├── business/           # Business domain models
│   │   │   ├── component/      # Component hierarchy
│   │   │   ├── identity/       # User and UserGroup
│   │   │   ├── project/       # Project and BusinessService
│   │   │   ├── request/        # DTOs for requests
│   │   │   ├── technology/     # Technology and Version
│   │   │   └── constant/       # Enums and constants
│   │   └── technical/          # Technical models
│   │       ├── Event.java
│   │       ├── EventType.java
│   │       ├── UniqueEntity.java
│   │       └── VersionedSavedEntity.java
│   ├── service/                # Domain services
│   │   ├── ProjectService.java
│   │   ├── BusinessServiceService.java
│   │   ├── ComponentService.java
│   │   ├── EnvironmentService.java
│   │   └── IdentityService.java
│   └── ports/                  # Hexagonal Architecture ports
│       ├── inputs/             # Inbound ports (driving)
│       └── outputs/            # Outbound ports (driven)
└── pom.xml
```

## Key Features

### Domain Models
- **Project**: Business projects with environments and components
- **BusinessService**: High-level business services grouping projects
- **Component**: Abstract base for Host, VirtualMachine, Hardware, Software
- **Environment**: Deployment environments (DEV, TEST, PROD, etc.)
- **Technology**: Technology stack definitions with version requirements
- **Version**: Semantic versioning (major.minor.patch)
- **User & UserGroup**: Identity management for access control

### Business Logic
- Multi-layer validation (field, business rules, cross-entity)
- Event tracking and audit trails
- State management and lifecycle
- Relationship validation
- Cascading operations (archive, delete)

### Architecture
- Hexagonal Architecture (Ports & Adapters)
- Rich Domain Model pattern
- Immutable value objects
- Separation of concerns between domain and infrastructure

## Quick Links

- [Architecture Overview](../architecture.md)
- [API Documentation](../api.md)
- [ADRs](../../../adr/)
