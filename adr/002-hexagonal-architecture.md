# ADR-002: Hexagonal Architecture

## Status

Accepted

## Context

The CMDB needs to:
- Maintain a clean separation between domain logic and infrastructure concerns
- Support multiple data sources (PostgreSQL, external APIs, etc.)
- Allow for easy testing of business logic without infrastructure dependencies
- Be resilient to technology changes over time

The domain layer (`core/`) contains the business logic and should be independent of:
- Persistence mechanisms
- UI frameworks
- External services
- Message brokers

## Decision

We adopt **Hexagonal Architecture** (also known as Ports and Adapters) for the core domain logic. This pattern:

```
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer (core/)                     │
│  ┌──────────────┐    ┌──────────────┐    ┌────────────┐  │
│  │   Entities   │    │   Use Cases  │    │  Services   │  │
│  │              │    │              │    │             │  │
│  └──────┬───────┘    └──────┬───────┘    └──────┬──────┘  │
│         │                  │                  │           │
│         ▼                  ▼                  ▼           │
│  ┌─────────────────────────────────────────────────────┐  │
│  │                   Ports (Interfaces)                   │  │
│  │  - Repository Ports                                  │  │
│  │  - API Client Ports                                  │  │
│  │  - Event Publisher Ports                             │  │
│  └─────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
         │                  │                  │
         ▼                  ▼                  ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  PostgreSQL  │    │ External API │    │  Kafka/Rabbit │
│   Adapter    │    │    Adapter   │    │     MQ       │
└──────────────┘    └──────────────┘    │    Adapter   │
                                       └──────────────┘
```

### Structure in `core/`

```
core/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── haingue/
│   │   │           └── cmdb/
│   │   │               └── core/
│   │   │                   ├── domain/           # Domain entities and value objects
│   │   │                   │   ├── model/        # Entity classes
│   │   │                   │   └── ports/        # Interfaces (Ports)
│   │   │                   │       ├── in/       # Inbound ports (driving)
│   │   │                   │       └── out/      # Outbound ports (driven)
│   │   │                   ├── application/      # Use cases and application services
│   │   │                   │   └── ports/        # Application-level ports
│   │   │                   └── exception/        # Domain exceptions
│   │   └── resources/
│   └── test/
│       └── java/
│           └── com/
│               └── haingue/
│                   └── cmdb/
│                       └── core/
└── pom.xml
```

### Key Principles

1. **Domain layer has NO dependencies** on infrastructure (no Spring, no JPA, no database drivers)
2. **Ports are interfaces** that define how the domain interacts with the outside world
3. **Adapters implement ports** and handle the technical details
4. **Dependencies flow inward**: Infrastructure depends on domain, not the other way around

## Consequences

### Positive
- **Testability**: Domain logic can be unit tested without mocking complex infrastructure
- **Maintainability**: Clear separation of concerns makes code easier to understand
- **Flexibility**: Can swap out adapters without changing domain logic
- **Technology agnostic**: Domain is protected from technology changes

### Negative
- **Initial complexity**: More files and abstraction to set up
- **Learning curve**: Team needs to understand Hexagonal Architecture patterns
- **Boilerplate**: More interfaces and delegation code

## Alternatives Considered

### 1. Traditional Layered Architecture
- **Rejected**: Tight coupling between layers, hard to test, hard to change
- **Rejected**: Domain logic often leaks into infrastructure layer

### 2. Clean Architecture (Uncle Bob)
- **Considered**: Very similar to Hexagonal, but with more strict dependency rules
- **Rejected**: Hexagonal is simpler and more pragmatic for our needs

### 3. DDD (Domain-Driven Design) with Onion Architecture
- **Considered**: Good for complex domains
- **Rejected**: More complex than needed for current requirements
- **Note**: We may evolve towards this if domain complexity increases
