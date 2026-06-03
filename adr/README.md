# Architecture Decision Records (ADRs)

This directory contains Architecture Decision Records (ADRs) for the CMDB project. ADRs document the architectural decisions made during the project's development, including the rationale, alternatives considered, and consequences.

## What is an ADR?

An ADR is a document that captures an important architectural decision made along with its context and the consequences of that decision. It's a lightweight way to track the "why" behind architectural choices.

## ADR Format

Each ADR follows a standard format:

```markdown
# ADR-00X: Decision Title

## Status

Accepted | Proposed | Rejected | Deprecated | Superseded by ADR-XXX

## Context

The issue that we're seeing that is motivating this decision or change.

## Decision

The change that we're proposing and/or doing.

## Consequences

The consequences of this decision, both positive and negative.

## Alternatives Considered

Other options that were considered and why they were rejected.
```

## Current ADRs

| ADR # | Title | Status | Date |
|-------|-------|--------|------|
| [ADR-001](001-monorepo-structure.md) | Monorepo Structure | Accepted | 2025-06-03 |
| [ADR-002](002-hexagonal-architecture.md) | Hexagonal Architecture | Accepted | 2025-06-03 |
| [ADR-003](003-service-organization.md) | Service Organization | Accepted | 2025-06-03 |
| [ADR-004](004-technology-stack.md) | Technology Stack | Accepted | 2025-06-03 |

## Historical Architecture Diagrams

For historical context, see:
- [Architecture.md](Architecture.md) - Contains PlantUML diagrams showing the evolution of the architecture
- [business.md](business.md) - Business use cases and domain model diagrams

## How to Add a New ADR

1. Create a new file in this directory with the format `ADR-XXX-title.md` where XXX is the next sequential number
2. Follow the ADR format template above
3. Submit a PR for review
4. Once accepted, update this README.md with the new ADR entry

## References

- [ADR GitHub Repository](https://github.com/joelcostigliola/async-await-adr)
- [MADR (Markdown Any Decision Records)](https://adr.github.io/madr/)
