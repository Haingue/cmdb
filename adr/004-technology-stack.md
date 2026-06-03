# ADR-004: Technology Stack

## Status

Accepted

## Context

The CMDB project needs a technology stack that:
- Is well-supported and maintainable
- Has good performance characteristics
- Is familiar to the development team
- Allows for rapid development
- Scales well with the project

## Decision

### Backend Services (Java)

| Component | Technology | Version | Rationale |
|-----------|------------|---------|-----------|
| Core Domain | Java | 17+ | Enterprise-grade, type-safe, good ecosystem |
| Spring Boot | Spring Boot | 3.x | Industry standard for Java microservices |
| Build Tool | Maven | 3.8+ | Familiar, good dependency management |
| Database | PostgreSQL | 15+ | Relational, ACID, good performance |
| ORM | JPA/Hibernate | 6.x | Industry standard, good integration with Spring |
| API | REST + Spring Web | 6.x | Standard for web services |
| Validation | Jakarta Validation | 3.0 | Standard validation framework |
| Testing | JUnit 5 + Testcontainers | - | Modern testing, integration test support |

### Frontend (React)

| Component | Technology | Version | Rationale |
|-----------|------------|---------|-----------|
| Framework | React | 18+ | Popular, good ecosystem, component-based |
| Language | TypeScript | 5.x | Type safety, better developer experience |
| State Management | React Query / Context API | - | Simple, effective |
| Styling | CSS Modules / Tailwind | - | Maintainable styling |
| Build Tool | Vite / Create React App | - | Fast development experience |
| Package Manager | npm / pnpm | - | Standard JavaScript package management |

### Data Aggregators

| Component | Technology | Version | Rationale |
|-----------|------------|---------|-----------|
| GitHub Analyzer | Spring Boot + GitHub API | 3.x | Consistent with other services |
| Syslog Server | Spring Boot + Syslog libraries | 3.x | Consistent with other services |

### Documentation

| Component | Technology | Rationale |
|-----------|------------|-----------|
| Framework | Astro + Starlight | Modern, fast, markdown-based |
| Language | Markdown + MDX | Easy to write and maintain |
| Styling | Tailwind CSS | Consistent with modern web standards |

### Infrastructure

| Component | Technology | Rationale |
|-----------|------------|-----------|
| Containerization | Docker | Standard container platform |
| Orchestration | Docker Compose | Simple multi-container management |
| CI/CD | GitHub Actions | Integrated with GitHub, free for open source |

## Consequences

### Positive
- **Team familiarity**: Java + Spring Boot is familiar to the team
- **Enterprise support**: All technologies are well-supported in enterprise environments
- **Good ecosystem**: Rich library and tooling support
- **Type safety**: Java and TypeScript both provide strong typing
- **Modern stack**: Uses current versions of all technologies

### Negative
- **Java verbosity**: More boilerplate code compared to dynamic languages
- **Multiple languages**: Need to maintain expertise in Java, TypeScript, JavaScript
- **Learning curve**: Some team members may need to learn React or Spring Boot

## Alternatives Considered

### Backend Alternatives

#### 1. Kotlin
- **Considered**: More concise than Java, fully interoperable
- **Rejected**: Team has less experience with Kotlin
- **Note**: Could be introduced gradually in the future

#### 2. Node.js
- **Considered**: JavaScript/TypeScript for full-stack consistency
- **Rejected**: Team has more Java experience
- **Rejected**: Less type safety than Java

#### 3. Quarkus
- **Considered**: Faster startup, native compilation
- **Rejected**: Less familiar to the team
- **Note**: Could be evaluated for specific use cases

### Frontend Alternatives

#### 1. Vue.js
- **Considered**: Progressive framework, easier learning curve
- **Rejected**: Team preference for React
- **Rejected**: Smaller ecosystem than React

#### 2. Angular
- **Considered**: Enterprise-grade, TypeScript-first
- **Rejected**: More complex than React
- **Rejected**: Steeper learning curve

#### 3. Svelte
- **Considered**: Simpler, compiles to vanilla JS
- **Rejected**: Smaller ecosystem
- **Rejected**: Less team experience

### Database Alternatives

#### 1. MySQL
- **Considered**: Familiar, widely used
- **Rejected**: PostgreSQL has better advanced features (JSON, CTEs, etc.)

#### 2. MongoDB
- **Considered**: Flexible schema, good for hierarchical data
- **Rejected**: CMDB data is relational by nature
- **Rejected**: No ACID transactions across documents

#### 3. Graph Database (Neo4j)
- **Considered**: Good for relationship-heavy data
- **Rejected**: Overkill for current needs
- **Rejected**: Team has less experience with graph databases
- **Note**: Could be considered for specific relationship queries in the future
