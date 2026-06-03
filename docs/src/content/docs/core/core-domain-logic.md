---
title: CORE Domain Business Logic
description: Complete documentation of CMDB CORE business logic, validation chains, event management, and domain rules
---

# CORE Domain Business Logic

This document describes all business logic implemented in the CMDB CORE module, including model validation, event management, validation chains, and domain-specific rules.

## Overview

The CORE module implements a rich domain model with embedded business logic following Domain-Driven Design (DDD) principles. All validation, business rules, and workflows are encapsulated within the domain entities and services.

### Architecture Principles

```
┌─────────────────────────────────────────────────────────────────┐
│                      CORE Domain Layer                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐    │
│  │   Domain        │  │   Domain        │  │   Technical     │    │
│  │   Entities      │  │   Services      │  │   Models        │    │
│  │                 │  │                 │  │                 │    │
│  │ - Project       │  │ - Project       │  │ - Event         │    │
│  │ - BusinessService│  │ - Component     │  │ - EventType     │    │
│  │ - Component     │  │ - Environment   │  │ - UniqueEntity  │    │
│  │ - Environment   │  │ - BusinessService││ - Versioned     │    │
│  │ - Technology    │  │ - Technology    │  │   SavedEntity   │    │
│  │ - Version       │  │                 │  │                 │    │
│  │ - User          │  │                 │  │                 │    │
│  │ - UserGroup     │  │                 │  │                 │    │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                    Validation Layer                           │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │  │
│  │  │ Integrity    │  │ Business     │  │ Cross-Entity     │  │  │
│  │  │ Checks       │  │ Rules         │  │ Validation       │  │  │
│  │  └──────────────┘  └──────────────┘  └──────────────────┘  │  │
│  └─────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                    Event Management Layer                     │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │  │
│  │  │ Event        │  │ Event        │  │ Audit           │  │  │
│  │  │ Creation     │  │ Processing    │  │ Trail           │  │  │
│  │  └──────────────┘  └──────────────┘  └──────────────────┘  │  │
│  └─────────────────────────────────────────────────────────────┘  │
│                                                                      │
└─────────────────────────────────────────────────────────────────┘
```

## Validation Strategy

### Validation Layers

The CORE implements a multi-layer validation approach:

```
1. Field-Level Validation (Entity Methods)
   ├─ Null/Blank checks
   ├─ Format validation (regex, length, etc.)
   └─ Basic type validation
   
2. Business Rule Validation (Entity Methods)
   ├─ Domain-specific constraints
   ├─ Conditional validation
   └─ State validation
   
3. Cross-Entity Validation (Service Layer)
   ├─ Relationship validation
   ├─ Uniqueness checks
   └─ Referential integrity
   
4. Workflow Validation (Service Layer)
   ├─ State transitions
   ├─ Permission checks
   └─ Business process rules
```

### Validation Chain Pattern

Each entity follows a consistent validation chain:

```java
// 1. Pre-creation validation
entity.checkIntegrity();  // Basic field validation

// 2. Business rule validation
entity.validateBusinessRules(context);

// 3. Cross-entity validation (in service)
service.validateRelationships(entity, context);

// 4. State transition validation
entity.canTransitionTo(targetState);

// 5. Final persistence validation
repository.validateBeforeSave(entity);
```

## Entity Hierarchy

### Base Classes

```
┌─────────────────────────────────────────────────────────────┐
│                        UniqueEntity                            │
│  - uuid: UUID                                               │
│  + equals()                                                 │
│  + hashCode()                                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   VersionedSavedEntity                         │
│  - revision: long                                          │
│  - events: List<Event>                                     │
│  - creationDatetime: LocalDateTime                         │
│  - archiveDatetime: LocalDateTime                          │
│  + update(initiator: String): boolean                      │
│  + update(user: User): boolean                              │
│  + archive(initiator: String)                              │
│  + archive(user: User)                                     │
│  + isArchived(): boolean                                    │
└─────────────────────────────────────────────────────────────┘
                              │
         ┌────────────────────┼────────────────────┐
         ▼                    ▼                    ▼
   ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
   │   Project   │      │  Component  │      │ Environment │
   │             │      │ (Abstract)  │      │             │
   └─────────────┘      └──────┬──────┘      └─────────────┘
                              │
              ┌────────────────┼────────────────┐
              ▼                ▼                ▼
        ┌───────────┐    ┌───────────┐    ┌───────────┐
        │   Host    │    │  Virtual  │    │ Hardware  │
        │           │    │  Machine  │    │           │
        └───────────┘    └───────────┘    └───────────┘
```

## Domain Entities

### Project

**Purpose**: Represents a business project or initiative that owns a set of environments and components.

**Key Responsibilities**:
- Maintain project metadata (name, description, business service)
- Manage ownership and maintenance groups
- Track associated environments
- Validate project integrity

**Validation Rules**:
- `fullName`: Required, non-blank
- `shortName`: Required, non-blank, unique
- `description`: Required, non-blank
- `businessService`: Required, must be valid
- `owners`: Required, must have at least one member
- `maintainers`: Optional, but if present must be valid

**Business Rules**:
- A project must belong to exactly one BusinessService
- A project must have at least one owner group
- Project shortName must be unique across the system
- Archiving a project archives all its environments

**Lifecycle**:
```
CREATE → ACTIVE → ARCHIVED → DELETED
```

**Methods**:
- `checkIntegrity()`: Validates required fields
- `addEnvironment(Environment)`: Adds environment to project
- `removeEnvironment(Environment)`: Removes environment from project

### BusinessService

**Purpose**: Represents a high-level business service that groups related projects.

**Key Responsibilities**:
- Define business service metadata
- Provide unique abbreviation for identification
- Validate service definition

**Validation Rules**:
- `name`: Required, non-blank
- `abbreviation`: Required, exactly 3 characters, unique

**Business Rules**:
- Abbreviation must be exactly 3 uppercase letters
- BusinessService names must be unique
- Abbreviations must be unique across the system

**Methods**:
- `checkIntegrity()`: Validates name and abbreviation

### Component (Abstract)

**Purpose**: Abstract base class for all technical components (servers, software, etc.)

**Key Responsibilities**:
- Define common component attributes
- Track technology and version information
- Validate component integrity
- Support visitor pattern for type-specific operations
- Determine if component needs update

**Validation Rules**:
- `name`: Required, non-blank
- `type`: Required, non-null
- `technology`: Required, non-null
- `version`: Required, non-null

**Business Rules**:
- Component type cannot be changed after creation
- Version must be compatible with technology
- Certificate (if present) must be valid

**Methods**:
- `checkIntegrity()`: Validates required fields
- `accept(ComponentVisitor<T>)`: Abstract method for visitor pattern
- `needsUpdate()`: Checks if version is below minimal version
- `updateFrom(Component)`: Updates mutable fields from another component

**Concrete Types**:
- **Host**: Physical server
- **VirtualMachine**: Virtual server instance
- **Hardware**: Physical hardware device
- **Software**: Software application or service

### Environment

**Purpose**: Represents a deployment environment (DEV, TEST, PROD, etc.)

**Key Responsibilities**:
- Define environment metadata
- Track environment type and status
- Associate with a project
- Manage components deployed in this environment

**Validation Rules**:
- `name`: Required, non-blank
- `location`: Required, non-blank
- `type`: Required, valid EnvironmentType
- `status`: Required, valid EnvironmentStatus

**Business Rules**:
- Environment name must be unique within a project
- Type and status must be from defined enums
- Network area must be valid

### Technology

**Purpose**: Defines a technology stack (Java, PostgreSQL, Kubernetes, etc.)

**Key Responsibilities**:
- Define technology metadata
- Track version requirements
- Determine if a version needs update

**Validation Rules**:
- `name`: Required, non-blank
- `type`: Required, valid TechnologyType
- `minimalVersion`: Required if versions are tracked
- `targetVersion`: Optional
- `lastVersion`: Optional

**Business Rules**:
- If minimalVersion is set, any component using this technology with version < minimalVersion needs update
- Version comparison follows semantic versioning

**Methods**:
- `needsUpdate(Version)`: Returns true if version < minimalVersion

### Version

**Purpose**: Represents a semantic version (major.minor.patch)

**Key Responsibilities**:
- Parse and format version strings
- Compare versions
- Increment version components

**Validation Rules**:
- Version string must match pattern: `[0-9]+.[0-9]+.[0-9]+`
- All components must be non-negative integers

**Methods**:
- `fromString(String)`: Static factory method
- `majorRelease()`: Increments major version, resets minor and patch
- `minorRelease()`: Increments minor version, resets patch
- `patch()`: Increments patch version
- `compareTo(Version)`: Compares versions (implements Comparable)

### User & UserGroup

**Purpose**: Represents users and their group memberships for access control

**Key Responsibilities**:
- Define user identity
- Manage group memberships
- Validate user and group definitions

**Validation Rules (UserGroup)**:
- `name`: Required, non-blank
- `owner`: Required, non-null
- `members`: Required, non-empty
- `email`: Optional, but if present must be valid format

**Business Rules**:
- Every group must have exactly one owner
- A group must have at least one member
- Owner must be a member of the group (implicit)

**Methods**:
- `checkIntegrity()`: Validates group definition

## Event Management

### Event Model

**Purpose**: Tracks all significant changes to domain entities for audit and history purposes.

**EventType Enum**:
```java
public enum EventType {
    CREATE,    // Entity was created
    UPDATE,    // Entity was modified
    ARCHIVE,   // Entity was archived (soft delete)
    DELETE     // Entity was permanently deleted
}
```

**Event Fields**:
- `timestamp`: When the event occurred (Instant)
- `type`: What type of event (EventType)
- `description`: Human-readable description of the change
- `initiator`: Who initiated the change (user UUID or system name)

### Event Lifecycle

```
1. Action initiated by user/system
   │
2. Entity state is modified
   │
3. Event is created with:
   - type: CREATE/UPDATE/ARCHIVE/DELETE
   - initiator: user.uuid.toString() or system name
   - description: optional details
   │
4. Event is added to entity.events list
   │
5. Entity revision is incremented
   │
6. Entity is persisted with new event
```

### Event Creation Patterns

**In VersionedSavedEntity**:
```java
// Direct update with event
public boolean update(Event event) {
    this.increaseVersion();
    return this.events.add(event);
}

// Update with user
public boolean update(User user) {
    return this.update(user.uuid().toString());
}

// Update with string initiator
public boolean update(String initiator) {
    return this.update(new Event(EventType.UPDATE, null, initiator));
}
```

**In Services**:
```java
// When creating an entity
Project project = Project.builder()
    .fullName(fullName)
    .shortName(shortName)
    // ... other fields
    .build();

// Add creation event
project.update(new Event(
    EventType.CREATE,
    "Project created: " + shortName,
    initiator.uuid().toString()
));

// When updating an entity
existingProject.setFullName(project.getFullName());
existingProject.update(initiator);  // Automatically creates UPDATE event
```

### Audit Trail

Every VersionedSavedEntity maintains:
- `revision`: Monotonically increasing version number
- `events`: Complete history of all changes
- `creationDatetime`: When entity was created
- `archiveDatetime`: When entity was archived (null if active)

**Audit Query Patterns**:
```java
// Get all events for an entity
List<Event> allEvents = entity.getEvents();

// Get events of specific type
List<Event> createEvents = allEvents.stream()
    .filter(e -> e.getType() == EventType.CREATE)
    .collect(Collectors.toList());

// Get last update
Optional<Event> lastUpdate = allEvents.stream()
    .filter(e -> e.getType() == EventType.UPDATE)
    .reduce((first, second) -> second);

// Get initiator of last change
String lastInitiator = entity.getEvents().stream()
    .reduce((first, second) -> second)
    .map(Event::getInitiator)
    .orElse("system");
```

## Validation Chains

### Project Validation Chain

```
Project Creation:
┌─────────────────────────────────────────────────────────────┐
│ 1. Field Validation (checkIntegrity)                          │
│    ├─ fullName: not blank                                     │
│    ├─ shortName: not blank                                     │
│    ├─ description: not blank                                  │
│    ├─ businessService: not null, name not blank               │
│    └─ owners: not null                                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. Business Rule Validation                                 │
│    ├─ shortName: unique across all projects                   │
│    ├─ businessService: exists in database                      │
│    ├─ owners: has at least one member                         │
│    └─ maintainers: if present, must be valid group             │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Cross-Entity Validation                                 │
│    ├─ businessService: not archived                           │
│    ├─ owners: not archived                                    │
│    └─ maintainers: not archived (if present)                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Permission Validation                                   │
│    └─ initiator: must have permission to create projects      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. Persistence Validation                                 │
│    └─ No duplicate shortName in database                       │
└─────────────────────────────────────────────────────────────┘
```

### Component Validation Chain

```
Component Creation:
┌─────────────────────────────────────────────────────────────┐
│ 1. Field Validation (checkIntegrity)                          │
│    ├─ name: not blank                                         │
│    ├─ type: not null                                          │
│    ├─ technology: not null                                    │
│    └─ version: not null                                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. Type-Specific Validation                                │
│    ├─ Host: hostname format, IP address format                │
│    ├─ VirtualMachine: valid hypervisor reference              │
│    ├─ Hardware: valid serial number format                    │
│    └─ Software: valid package name                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Business Rule Validation                                 │
│    ├─ technology: exists in catalog                           │
│    ├─ version: valid format (x.y.z)                           │
│    ├─ version: >= technology.minimalVersion (if defined)      │
│    └─ certificate: if present, not expired                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Relationship Validation                                 │
│    └─ Can be linked to environment (validated in service)     │
└─────────────────────────────────────────────────────────────┘
```

### BusinessService Validation Chain

```
BusinessService Creation:
┌─────────────────────────────────────────────────────────────┐
│ 1. Field Validation (checkIntegrity)                          │
│    ├─ name: not blank                                         │
│    └─ abbreviation: not blank, exactly 3 characters             │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. Business Rule Validation                                 │
│    ├─ abbreviation: exactly 3 characters                       │
│    └─ abbreviation: uppercase letters only                     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Uniqueness Validation                                   │
│    ├─ name: unique across all business services                │
│    └─ abbreviation: unique across all business services       │
└─────────────────────────────────────────────────────────────┘
```

## Business Rules

### Project Rules

1. **Unique Identification**: Project `shortName` must be unique across the entire system
2. **Business Service Requirement**: Every project must belong to exactly one BusinessService
3. **Ownership Requirement**: Every project must have at least one owner group
4. **Cascading Archive**: Archiving a project automatically archives all its environments
5. **Environment Exclusivity**: An environment can belong to only one project
6. **Name Constraints**: 
   - `fullName`: 3-100 characters
   - `shortName`: 3-20 characters, uppercase letters, numbers, and hyphens only

### BusinessService Rules

1. **Abbreviation Format**: Must be exactly 3 uppercase letters
2. **Unique Abbreviation**: Abbreviation must be unique across all business services
3. **Name Uniqueness**: BusinessService name must be unique
4. **Immutability**: Once created, abbreviation cannot be changed

### Component Rules

1. **Type Hierarchy**: Component type is immutable after creation
2. **Version Requirement**: Every component must have a version
3. **Technology Requirement**: Every component must have a technology
4. **Update Notification**: Components with version < technology.minimalVersion need update
5. **Certificate Validation**: If certificate is present, it must be valid and not expired
6. **Name Uniqueness**: Component name must be unique within its environment

### Environment Rules

1. **Type Constraints**: Environment type must be from defined enum (DEV, TEST, STAGING, PROD, etc.)
2. **Status Constraints**: Environment status must be from defined enum
3. **Location Requirement**: Every environment must have a location
4. **Network Area**: Must be from defined NetworkArea enum
5. **Component Association**: An environment can contain multiple components

### Technology Rules

1. **Version Requirements**:
   - `minimalVersion`: Minimum acceptable version
   - `targetVersion`: Recommended version
   - `lastVersion`: Latest stable version
2. **Update Logic**: `technology.needsUpdate(component.version)` returns true if component version < minimalVersion
3. **Type Classification**: Technology type must be from TechnologyType enum
4. **Programming Language**: Optional, but if present must be valid

### User & UserGroup Rules

1. **Group Ownership**: Every group must have exactly one owner
2. **Membership Requirement**: A group must have at least one member
3. **Owner Membership**: The owner of a group is implicitly a member
4. **Name Uniqueness**: UserGroup name must be unique

## Workflows

### Project Creation Workflow

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Request    │────▶│  Validation  │────▶│   Create    │
│   Received   │     │   (Service)  │     │   Project   │
└──────────────┘     └──────────────┘     └──────────────┘
                                                        │
                                                        ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│ Validate     │◀────│   Business   │     │   Create    │
│ Business     │     │   Service    │     │   Environ-  │
│ Service      │     │   Existence  │     │   ments     │
└──────────────┘     └──────────────┘     └──────────────┘
        │                     │                        │
        ▼                     ▼                        ▼
┌─────────────────────────────────────────────────────────────┐
│                        Rollback if any step fails              │
└─────────────────────────────────────────────────────────────┘
```

**Steps**:
1. Receive ProjectCreationRequest
2. Validate request (not null, all required fields present)
3. Validate BusinessService exists (if specified by name)
4. Create Project entity
5. Call project.checkIntegrity()
6. Validate uniqueness of shortName
7. Save Project
8. Create CREATE event
9. Return saved Project

### Component Update Workflow

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Find       │────▶│  Validate    │────▶│   Check      │
│   Existing   │     │   New        │     │   Changes    │
└──────────────┘     │   Version    │     │   (needs    │
                     └──────────────┘     │    update?)  │
                                             └──────────────┘
                                                    │
                                                    ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Update     │◀────│   Validate   │◀────│   Notify     │
│   Fields     │     │   Business   │     │   Maintainers│
│              │     │   Rules      │     │   (if        │
└──────────────┘     └──────────────┘     │    version   │
                                            │    changed)  │
                                            └──────────────┘
```

**Steps**:
1. Find existing component by UUID
2. Validate new component data
3. Check if version needs update (using Technology.needsUpdate())
4. Update mutable fields
5. Validate business rules (certificate, version compatibility)
6. Create UPDATE event
7. If version changed and needs update, notify maintainers
8. Save updated component
9. Return updated component

### Environment Archival Workflow

```
┌──────────────┐     ┌──────────────┐
│   Find       │────▶│  Validate    │
│   Environment│     │   Permissions│
└──────────────┘     └──────────────┘
                              │
                              ▼
┌──────────────┐     ┌──────────────┐
│   Archive    │────▶│   Archive    │
│   Environment│     │   All        │
│              │     │   Components │
└──────────────┘     └──────────────┘
                              │
                              ▼
┌──────────────┐     ┌──────────────┐
│   Create     │────▶│   Notify     │
│   ARCHIVE    │     │   Owners     │
│   Event      │     │              │
└──────────────┘     └──────────────┘
```

**Steps**:
1. Find environment by UUID
2. Validate initiator has permission to archive
3. Set archiveDatetime to now
4. For each component in environment:
   - Archive component (set archiveDatetime)
   - Create ARCHIVE event for component
5. Create ARCHIVE event for environment
6. Increment revision
7. Save environment
8. Notify owners

## Cross-Entity Relationships

### Project ↔ Environment

- **Cardinality**: One-to-Many (Project has many Environments)
- **Ownership**: Project owns Environments
- **Cascade**: Archive/Delete Project → Archive/Delete all Environments
- **Validation**: Environment can belong to only one Project

### Environment ↔ Component

- **Cardinality**: One-to-Many (Environment contains many Components)
- **Ownership**: Environment contains Components
- **Cascade**: Archive Environment → Archive all Components
- **Validation**: Component can belong to only one Environment

### Project ↔ BusinessService

- **Cardinality**: Many-to-One (Many Projects belong to one BusinessService)
- **Ownership**: BusinessService groups Projects
- **Validation**: Project must have exactly one BusinessService

### Component ↔ Technology

- **Cardinality**: Many-to-One (Many Components use one Technology)
- **Ownership**: Technology defines valid versions
- **Validation**: Component version must be compatible with Technology

### User ↔ UserGroup

- **Cardinality**: Many-to-Many (User belongs to many UserGroups, UserGroup has many Users)
- **Ownership**: UserGroup has one owner (who is also a member)
- **Validation**: UserGroup must have at least one member

## Exception Handling

### Exception Hierarchy

```
CoreException (Base exception for all CORE errors)
├── InvalidObjectException
│   ├── FieldValidationException
│   ├── BusinessRuleException
│   └── IntegrityCheckException
│
├── NotFoundException
│   └── EntityNotFoundException
│
└── OperationException
    ├── UnauthorizedException
    ├── ConflictException
    └── StateTransitionException
```

### Exception Usage Patterns

**InvalidObjectException**:
```java
public void checkIntegrity() {
    if (StringUtils.isBlank(fullName)) 
        throw new InvalidObjectException("fullName cannot be blank", this);
    if (StringUtils.isBlank(shortName)) 
        throw new InvalidObjectException("shortName cannot be blank", this);
}
```

**NotFoundException**:
```java
public Project findOne(UUID uuid, User initiator) {
    return this.projectOutputPort.findOne(uuid)
            .orElseThrow(() -> new NotFoundException(
                "Project with id " + uuid.toString() + " not found"));
}
```

**CoreException**:
```java
public Project update(Project project, User initiator) {
    if (project == null) 
        throw new CoreException("Project cannot be null");
    if (project.getUuid() == null) 
        throw new CoreException("Project uuid cannot be null");
    // ...
}
```

## Notification System

### Notification Triggers

The CORE identifies the following notification triggers:

1. **Version Update Required**: When a Component's version < Technology.minimalVersion
2. **Certificate Expiration**: When a Component's certificate is about to expire (30 days warning)
3. **Ownership Change**: When Project owners or maintainers are changed
4. **Environment Status Change**: When Environment status changes (especially to PROD)
5. **Archival**: When Project, Environment, or Component is archived
6. **Creation**: When new Project or Component is created

### Notification Payload

Notifications should include:
- Entity type and UUID
- Change type (CREATE, UPDATE, ARCHIVE, DELETE)
- Changed fields (for UPDATE)
- Timestamp
- Initiator
- Affected users/groups (recipients)

**Note**: Actual notification delivery (email, Slack, etc.) is handled by infrastructure services, not the CORE.

## Implementation Checklist

### For Each Entity

- [ ] `checkIntegrity()` method implemented
- [ ] All required fields validated
- [ ] Business rules enforced
- [ ] Events created for all state changes
- [ ] Uniqueness constraints defined
- [ ] Relationship validation implemented
- [ ] Archive/delete cascading defined

### For Each Service

- [ ] Create operation with full validation
- [ ] Update operation with change detection
- [ ] Archive operation with cascading
- [ ] Delete operation (soft or hard)
- [ ] Find operations with proper filtering
- [ ] Permission checks (TODO in most services)
- [ ] Event creation for all mutations

### For Each Validation Chain

- [ ] Field-level validation
- [ ] Business rule validation
- [ ] Cross-entity validation
- [ ] Uniqueness validation
- [ ] Permission validation
- [ ] State transition validation

## Next Steps

1. Implement missing validation methods on entities
2. Add cross-entity validation in services
3. Implement notification triggers
4. Add more detailed event descriptions
5. Implement permission checking (currently TODO in many places)
6. Add state transition validation

## See Also

- [Validation Chains Documentation](./core-validation-chains.md)
- [Event Management Documentation](./core-event-management.md)
- [Business Rules Documentation](./core-business-rules.md)
- [Architecture Overview](../architecture.md)
