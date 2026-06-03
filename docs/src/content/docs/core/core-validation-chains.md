---
title: CORE Validation Chains
description: Detailed validation workflows and constraints for all CMDB domain entities
---

# CORE Validation Chains

This document provides detailed validation workflows for each domain entity, including field-level constraints, business rules, and cross-entity validation.

## Validation Framework

### Validation Layer Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Validation Pipeline                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐      │
│  │   Layer 1:   │    │   Layer 2:   │    │   Layer 3:   │      │
│  │  Field-      │───▶│  Business    │───▶│  Cross-      │      │
│  │  Level      │    │  Rules       │    │  Entity      │      │
│  │  Validation │    │  Validation  │    │  Validation  │      │
│  └──────────────┘    └──────────────┘    └──────────────┘      │
│                                 │                              │
│                                 ▼                              │
│                    ┌─────────────────┐                         │
│                    │   Layer 4:      │                         │
│                    │  Workflow      │                         │
│                    │  Validation    │                         │
│                    └─────────────────┘                         │
│                                                                      │
└─────────────────────────────────────────────────────────────────┘
```

### Validation Result Pattern

Each validation layer can:
1. **Pass**: Continue to next layer
2. **Fail with Exception**: Throw appropriate exception with details
3. **Collect Errors**: Accumulate multiple validation errors

## Entity-Specific Validation Chains

---

## Project Validation

### Creation Validation Chain

```
┌─────────────────────────────────────────────────────────────────┐
│  PROJECT CREATION VALIDATION CHAIN                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Step 1: Request Validation (Service Layer)                      │
│  ├─ projectCreationRequest != null                              │
│  ├─ projectCreationRequest.getRequestor() != null               │
│  └─ projectCreationRequest.getProject() != null                 │
│                                                                     │
│  Step 2: Field-Level Validation (Entity.checkIntegrity)          │
│  ├─ fullName: not blank, 3-100 characters                        │
│  │   ├── Min length: 3                                          │
│  │   ├── Max length: 100                                        │
│  │   └── Allowed chars: letters, numbers, spaces, -, _          │
│  │                                                               │
│  ├─ shortName: not blank, 3-20 characters                       │
│  │   ├── Min length: 3                                          │
│  │   ├── Max length: 20                                         │
│  │   ├── Allowed chars: uppercase letters, numbers, hyphen     │
│  │   └── Pattern: ^[A-Z0-9-]{3,20}$                              │
│  │                                                               │
│  ├─ description: not blank, 10-500 characters                    │
│  │   ├── Min length: 10                                         │
│  │   └── Max length: 500                                        │
│  │                                                               │
│  ├─ businessService: not null                                  │
│  │   └── businessService.name: not blank                        │
│  │                                                               │
│  └─ owners: not null                                            │
│      └── (UserGroup validation delegated to UserGroup)          │
│                                                                     │
│  Step 3: Business Rule Validation (Service Layer)               │
│  ├─ BusinessService exists in database                         │
│  │   └── businessServiceService.findOne() returns result      │
│  │                                                               │
│  ├─ Owners group exists in database                             │
│  │   └── identityService.findUserGroup() returns result        │
│  │                                                               │
│  ├─ Owners group has at least one member                        │
│  │   └── owners.members().size() > 0                           │
│  │                                                               │
│  └─ If maintainers specified:                                  │
│      ├─ Maintainers group exists                               │
│      └─ Maintainers group has at least one member               │
│                                                                     │
│  Step 4: Uniqueness Validation (Service Layer)                  │
│  ├─ shortName: unique across all projects                       │
│  │   └── !projectOutputPort.existsByShortName(shortName)       │
│  │                                                               │
│  └─ fullName + businessService: unique combination              │
│      └── !projectOutputPort.existsByFullNameAndBusinessService()│
│                                                                     │
│  Step 5: Cross-Entity Validation (Service Layer)                │
│  ├─ BusinessService is not archived                             │
│  │   └── businessService.isArchived() == false                  │
│  │                                                               │
│  └─ Owners group is not archived                                │
│      └── owners.isArchived() == false (if applicable)            │
│                                                                     │
│  Step 6: Permission Validation (Service Layer) - TODO           │
│  └─ Initiator has permission to create projects                 │
│      └── permissionService.canCreateProject(initiator)         │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Exception Mapping**:
- Field validation failures → `InvalidObjectException`
- BusinessService not found → `NotFoundException`
- UserGroup not found → `NotFoundException`
- Duplicate shortName → `ConflictException` (to be created)
- Permission denied → `UnauthorizedException` (to be created)

### Update Validation Chain

```
┌─────────────────────────────────────────────────────────────────┐
│  PROJECT UPDATE VALIDATION CHAIN                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Step 1: Existence Validation                                     │
│  ├─ Project exists with given UUID                               │
│  │   └── projectOutputPort.findOne(uuid) returns result         │
│  │                                                               │
│  └─ Project is not archived                                       │
│      └── existingProject.isArchived() == false                   │
│                                                                     │
│  Step 2: Field-Level Validation (checkIntegrity)                │
│  ├─ Same as creation (see above)                                 │
│  │                                                               │
│  └─ If changing shortName:                                       │
│      └── New shortName must pass all shortName constraints       │
│                                                                     │
│  Step 3: Business Rule Validation                                 │
│  ├─ If changing businessService:                                 │
│  │   ├─ New BusinessService exists                              │
│  │   └─ New BusinessService is not archived                      │
│  │                                                               │
│  ├─ If changing owners:                                          │
│  │   ├─ New owners group exists                                  │
│  │   ├─ New owners group has at least one member                 │
│  │   └─ New owners group is not archived                         │
│  │                                                               │
│  └─ If changing maintainers:                                     │
│      ├─ New maintainers group exists (if specified)              │
│      └─ New maintainers group is not archived                     │
│                                                                     │
│  Step 4: Uniqueness Validation                                   │
│  ├─ If changing shortName:                                       │
│  │   └── New shortName must be unique                            │
│  │                                                               │
│  └─ fullName + businessService combination must remain unique   │
│                                                                     │
│  Step 5: State Transition Validation                             │
│  └─ No state transitions for Project (only archive/delete)       │
│                                                                     │
│  Step 6: Change Detection & Event Creation                        │
│  ├─ Detect which fields changed                                   │
│  ├─ Create UPDATE event with description of changes              │
│  └─ If owners changed: notify new and previous owners            │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

### Archive Validation Chain

```
┌─────────────────────────────────────────────────────────────────┐
│  PROJECT ARCHIVE VALIDATION CHAIN                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Step 1: Existence & State Validation                             │
│  ├─ Project exists                                                  │
│  └─ Project is not already archived                                │
│                                                                     │
│  Step 2: Permission Validation - TODO                            │
│  └─ Initiator has permission to archive projects                  │
│                                                                     │
│  Step 3: Cascading Validation                                     │
│  └─ All environments can be archived (not in use by other projects)│
│                                                                     │
│  Step 4: Execute Archive                                           │
│  ├─ Set archiveDatetime to now                                     │
│  ├─ For each environment:                                         │
│  │   └─ environmentService.archive(environment.getUuid())         │
│  │                                                               │
│  ├─ Create ARCHIVE event                                           │
│  │   └── new Event(EventType.ARCHIVE, "Project archived", init)  │
│  │                                                               │
│  └─ Increment revision                                             │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

---

## BusinessService Validation

### Creation Validation Chain

```
┌─────────────────────────────────────────────────────────────────┐
│  BUSINESS SERVICE CREATION VALIDATION CHAIN                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Step 1: Field-Level Validation (checkIntegrity)                │
│  ├─ name: not blank, 3-50 characters                             │
│  │   ├── Min length: 3                                           │
│  │   ├── Max length: 50                                          │
│  │   └── Allowed chars: letters, numbers, spaces, -              │
│  │                                                                │
│  └─ abbreviation: not blank, exactly 3 characters                 │
│      ├── Length: exactly 3                                       │
│      ├── Allowed chars: uppercase letters only (A-Z)              │
│      └── Pattern: ^[A-Z]{3}$                                      │
│                                                                     │
│  Step 2: Business Rule Validation                                │
│  └─ Abbreviation format: must be all uppercase                    │
│      └── abbreviation.equals(abbreviation.toUpperCase())         │
│                                                                     │
│  Step 3: Uniqueness Validation                                   │
│  ├─ name: unique across all business services                     │
│  │   └── !businessServiceOutputPort.existsByName(name)           │
│  │                                                                │
│  └─ abbreviation: unique across all business services             │
│      └── !businessServiceOutputPort.existsByAbbreviation(abbr)    │
│                                                                     │
│  Step 4: Permission Validation - TODO                            │
│  └─ Initiator has permission to create business services         │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Special Constraints**:
- Abbreviation is **immutable** after creation
- Once created, abbreviation cannot be changed

---

## Component Validation

### Creation Validation Chain (Abstract Component)

```
┌─────────────────────────────────────────────────────────────────┐
│  COMPONENT CREATION VALIDATION CHAIN                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Step 1: Field-Level Validation (checkIntegrity)                │
│  ├─ name: not blank, 3-100 characters                             │
│  │   ├── Min length: 3                                           │
│  │   ├── Max length: 100                                         │
│  │   └── Pattern: ^[a-zA-Z0-9][a-zA-Z0-9_.-]{2,99}$              │
│  │                                                                │
│  ├─ type: not null                                               │
│  │   └── Must be valid ComponentType enum value                  │
│  │                                                                │
│  ├─ technology: not null                                         │
│  │   └── Must be valid Technology object                        │
│  │                                                                │
│  └─ version: not null                                            │
│      └── Must be valid Version object                            │
│                                                                     │
│  Step 2: Type-Specific Validation (Concrete Classes)             │
│  ├─ Host:                                                          │
│  │   ├─ hostname: valid hostname format (if applicable)          │
│  │   │   └── Pattern: ^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9])(\.( [a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]))*$ │
│  │   ├─ ipAddress: valid IPv4 or IPv6 format                     │
│  │   │   └── Pattern: ^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$ │
│  │   └─ os: not blank (if specified)                             │
│  │                                                                │
│  ├─ VirtualMachine:                                              │
│  │   ├─ hostname: valid hostname format                         │
│  │   ├─ hypervisor: not blank                                    │
│  │   └─ vmId: not blank (unique within hypervisor)              │
│  │                                                                │
│  ├─ Hardware:                                                   │
│  │   ├─ serialNumber: not blank, 5-50 characters                 │
│  │   │   └── Pattern: ^[A-Za-z0-9-]{5,50}$                        │
│  │   ├─ manufacturer: not blank                                 │
│  │   └─ model: not blank                                        │
│  │                                                                │
│  └─ Software:                                                   │
│      ├─ packageName: not blank                                  │
│      ├─ vendor: not blank (if specified)                         │
│      └─ licenseType: valid if specified                          │
│                                                                     │
│  Step 3: Business Rule Validation                                │
│  ├─ Technology exists in catalog                                 │
│  │   └── technologyService.findOne(technology.getName())       │
│  │                                                                │
│  ├─ Version format is valid                                     │
│  │   └── version.toString().matches("[0-9]+\.[0-9]+\.[0-9]+")    │
│  │                                                                │
│  ├─ Version compatibility:                                      │
│  │   └── If technology.minimalVersion is set:                   │
│  │       └── version.compareTo(technology.minimalVersion) >= 0  │
│  │                                                                │
│  └─ Certificate validation (if present):                        │
│      ├─ Certificate is not blank                                 │
│      ├─ Certificate format is valid                              │
│      └─ Certificate is not expired                               │
│                                                                     │
│  Step 4: Relationship Validation (Service Layer)                 │
│  └─ If linking to environment:                                   │
│      └── Environment exists and is not archived                 │
│                                                                     │
│  Step 5: Uniqueness Validation                                   │
│  └─ name: unique within environment (if specified)               │
│      └── !componentOutputPort.existsByNameAndEnvironment()      │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

### Update Validation Chain

```
┌─────────────────────────────────────────────────────────────────┐
│  COMPONENT UPDATE VALIDATION CHAIN                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Step 1: Existence Validation                                     │
│  ├─ Component exists with given UUID                              │
│  └─ Component is not archived                                     │
│                                                                     │
│  Step 2: Type Immutability Check                                  │
│  └─ Component type cannot be changed                              │
│      └── existingComponent.getType() == newComponent.getType()   │
│                                                                     │
│  Step 3: Field-Level Validation (checkIntegrity)                │
│  └─ Same as creation (see above)                                 │
│                                                                     │
│  Step 4: Version Update Check                                    │
│  ├─ If version changed:                                          │
│  │   ├─ New version format is valid                              │
│  │   ├─ New version is compatible with technology                │
│  │   └─ Check if update is needed:                               │
│  │       └── technology.needsUpdate(newVersion)                 │
│  │           → If true, set needsUpdate flag for notifications    │
│  │                                                               │
│  └─ If version not changed:                                      │
│      └─ Keep existing version                                   │
│                                                                     │
│  Step 5: Technology Update Check                                 │
│  ├─ If technology changed:                                       │
│  │   ├─ New technology exists                                    │
│  │   └─ New version is compatible with new technology            │
│  │                                                               │
│  └─ If technology not changed:                                   │
│      └─ Keep existing technology                                 │
│                                                                     │
│  Step 6: Certificate Validation (if present)                     │
│  └─ Same as creation                                             │
│                                                                     │
│  Step 7: Change Detection & Event Creation                       │
│  ├─ Detect which fields changed                                   │
│  ├─ Create UPDATE event with description                          │
│  └─ If version needs update:                                      │
│      └── Add notification for maintainers                        │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

### Special: needsUpdate() Logic

```java
// In Component class
public boolean needsUpdate() {
    return technology.needsUpdate(version);
}

// In Technology class
public boolean needsUpdate(Version version) {
    // Returns true if version < minimalVersion
    if (this.minimalVersion == null) {
        return false;  // No minimal version defined, no update needed
    }
    return version.compareTo(this.minimalVersion) < 0;
}
```

**Version Comparison Logic**:
- `version.compareTo(minimalVersion) < 0` → version is older, needs update
- `version.compareTo(minimalVersion) == 0` → version is minimal, no update needed
- `version.compareTo(minimalVersion) > 0` → version is newer, no update needed

---

## Environment Validation

### Creation Validation Chain

```
┌─────────────────────────────────────────────────────────────────┐
│  ENVIRONMENT CREATION VALIDATION CHAIN                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Step 1: Field-Level Validation                                  │
│  ├─ name: not blank, 3-50 characters                              │
│  │   ├── Min length: 3                                           │
│  │   ├── Max length: 50                                          │
│  │   └── Pattern: ^[a-zA-Z0-9][a-zA-Z0-9_-]{2,49}$                 │
│  │                                                                │
│  ├─ location: not blank, 2-100 characters                         │
│  │   ├── Min length: 2                                           │
│  │   └── Max length: 100                                         │
│  │                                                                │
│  ├─ type: not null                                               │
│  │   └── Must be valid EnvironmentType enum value:              │
│  │       {DEVELOPMENT, TEST, STAGING, PRODUCTION, PREPROD, UAT}   │
│  │                                                                │
│  └─ status: not null                                             │
│      └── Must be valid EnvironmentStatus enum value:             │
│          {PLANNED, CREATING, ACTIVE, MAINTENANCE, DECOMMISSIONED}│
│                                                                     │
│  Step 2: Network Area Validation                                  │
│  └─ networkArea: valid NetworkArea enum value                     │
│      └── {INTERNAL, DMZ, EXTERNAL, CLOUD, HYBRID}                │
│                                                                     │
│  Step 3: Business Rule Validation                                 │
│  └─ If jiraTracker specified:                                    │
│      └── JIRA tracker format is valid                             │
│          └── Pattern: ^[A-Z]+-\d+$ (e.g., PROJ-123)               │
│                                                                     │
│  Step 4: Relationship Validation (Service Layer)                 │
│  └─ Project must exist (if linking to project)                    │
│      └── projectService.findOne(projectId) returns result        │
│                                                                     │
│  Step 5: Uniqueness Validation                                   │
│  └─ name: unique within project                                   │
│      └── !environmentOutputPort.existsByNameAndProject()         │
│                                                                     │
│  Step 6: Cross-Entity Validation                                 │
│  └─ Project is not archived (if specified)                        │
│      └── project.isArchived() == false                           │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

### Update Validation Chain

```
┌─────────────────────────────────────────────────────────────────┐
│  ENVIRONMENT UPDATE VALIDATION CHAIN                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Step 1: Existence Validation                                     │
│  └─ Environment exists and is not archived                        │
│                                                                     │
│  Step 2: State Transition Validation                             │
│  ├─ Current state: PLANNED                                       │
│  │   └── Can transition to: CREATING                              │
│  │                                                               │
│  ├─ Current state: CREATING                                       │
│  │   └── Can transition to: ACTIVE, PLANNED                       │
│  │                                                               │
│  ├─ Current state: ACTIVE                                         │
│  │   └── Can transition to: MAINTENANCE, DECOMMISSIONED           │
│  │                                                               │
│  ├─ Current state: MAINTENANCE                                   │
│  │   └── Can transition to: ACTIVE, DECOMMISSIONED               │
│  │                                                               │
│  └─ Current state: DECOMMISSIONED                                │
│      └── Cannot transition to any state (terminal state)          │
│                                                                     │
│  Step 3: Status Change Notifications                             │
│  └─ If status changed to PRODUCTION:                            │
│      └── Notify all stakeholders                                  │
│                                                                     │
│  Step 4: Field-Level Validation                                   │
│  └─ Same as creation (see above)                                 │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

### State Transition Matrix

```
                    ┌──────────┬──────────┬─────────┬──────────────┬───────────────┐
                    │  To      │  To      │  To     │  To           │  To            │
From                │ PLANNED  │ CREATING │ ACTIVE  │ MAINTENANCE   │ DECOMMISSIONED │
────────────────────┼──────────┼──────────┼─────────┼──────────────┼───────────────┤
PLANNED             │    ✓     │    ✓     │    ✗    │      ✗        │       ✗        │
CREATING            │    ✓     │    ✗     │    ✓    │      ✗        │       ✗        │
ACTIVE              │    ✗     │    ✗     │    ✗    │      ✓        │       ✓        │
MAINTENANCE         │    ✗     │    ✗     │    ✓    │      ✗        │       ✓        │
DECOMMISSIONED      │    ✗     │    ✗     │    ✗    │      ✗        │       ✗        │
────────────────────────────────────────────────────────────────────
```

**✓** = Valid transition
**✗** = Invalid transition

---

## Technology Validation

### Creation Validation Chain

```
┌─────────────────────────────────────────────────────────────────┐
│  TECHNOLOGY CREATION VALIDATION CHAIN                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Step 1: Field-Level Validation                                  │
│  ├─ name: not blank, 3-50 characters                              │
│  │   ├── Min length: 3                                           │
│  │   ├── Max length: 50                                          │
│  │   └── Pattern: ^[a-zA-Z0-9][a-zA-Z0-9 .-]{2,49}$                 │
│  │                                                                │
│  ├─ type: not null                                               │
│  │   └── Must be valid TechnologyType enum value:               │
│  │       {PROGRAMMING_LANGUAGE, DATABASE, FRAMEWORK, TOOL,        │
│  │        INFRASTRUCTURE, PLATFORM, LIBRARY, SERVICE}           │
│  │                                                                │
│  └─ description: optional, 0-500 characters                        │
│      └── Max length: 500                                         │
│                                                                     │
│  Step 2: Version Validation (if versions specified)              │
│  ├─ minimalVersion: if present, must be valid Version             │
│  │   └── version.toString().matches("[0-9]+\.[0-9]+\.[0-9]+")    │
│  │                                                                │
│  ├─ targetVersion: if present, must be valid Version               │
│  │   └── Same pattern as minimalVersion                          │
│  │                                                                │
│  └─ lastVersion: if present, must be valid Version                 │
│      └── Same pattern as minimalVersion                          │
│                                                                     │
│  Step 3: Version Relationship Validation                         │
│  ├─ If minimalVersion and targetVersion both present:            │
│  │   └── minimalVersion.compareTo(targetVersion) <= 0             │
│  │       (minimal cannot be newer than target)                   │
│  │                                                                │
│  ├─ If minimalVersion and lastVersion both present:             │
│  │   └── minimalVersion.compareTo(lastVersion) <= 0               │
│  │                                                               │
│  └─ If targetVersion and lastVersion both present:              │
│      └── targetVersion.compareTo(lastVersion) <= 0               │
│                                                                     │
│  Step 4: Programming Language Validation                         │
│  └─ If present: must be valid programming language name          │
│      └── Allowed: Java, Python, JavaScript, TypeScript, C#, etc.    │
│                                                                     │
│  Step 5: Uniqueness Validation                                   │
│  └─ name: unique across all technologies                          │
│      └── !technologyOutputPort.existsByName(name)                 │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

### Version Comparison Logic

```java
// Version implements Comparable<Version>
public int compareTo(Version other) {
    if (this.major != other.major) {
        return this.major - other.major;
    }
    if (this.minor != other.minor) {
        return this.minor - other.minor;
    }
    return this.patch - other.patch;
}

// Usage examples:
Version v1 = new Version(1, 2, 3);  // 1.2.3
Version v2 = new Version(1, 2, 4);  // 1.2.4
Version v3 = new Version(1, 3, 0);  // 1.3.0
Version v4 = new Version(2, 0, 0);  // 2.0.0

v1.compareTo(v2) < 0;  // true: 1.2.3 < 1.2.4
v2.compareTo(v3) < 0;  // true: 1.2.4 < 1.3.0
v3.compareTo(v4) < 0;  // true: 1.3.0 < 2.0.0
v1.compareTo(v1) == 0; // true: equal
```

---

## UserGroup Validation

### Creation Validation Chain

```
┌─────────────────────────────────────────────────────────────────┐
│  USER GROUP CREATION VALIDATION CHAIN                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Step 1: Field-Level Validation (checkIntegrity)                │
│  ├─ name: not blank, 3-50 characters                              │
│  │   ├── Min length: 3                                           │
│  │   ├── Max length: 50                                          │
│  │   └── Pattern: ^[a-zA-Z0-9][a-zA-Z0-9_.-]{2,49}$                 │
│  │                                                                │
│  ├─ email: optional, but if present:                              │
│  │   └── Valid email format: ^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$   │
│  │                                                                │
│  ├─ description: optional, 0-500 characters                       │
│  │   └── Max length: 500                                         │
│  │                                                                │
│  ├─ owner: required, not null                                    │
│  │   └── Must be valid User object                               │
│  │                                                                │
│  └─ members: required, not null, not empty                       │
│      └── Must contain at least one User                          │
│                                                                     │
│  Step 2: Business Rule Validation                                │
│  ├─ Owner must be a member of the group (implicit)              │
│  │   └── members.contains(owner) is automatically true        │
│  │       (by construction, owner is added to members)           │
│  │                                                                │
│  └─ All members must be valid User objects                       │
│      └── Each user.uuid != null                                 │
│                                                                     │
│  Step 3: Uniqueness Validation                                   │
│  └─ name: unique across all user groups                           │
│      └── !identityOutputPort.existsUserGroupByName(name)          │
│                                                                     │
│  Step 4: Cross-Entity Validation                                 │
│  └─ Owner user must exist in database                            │
│      └── identityOutputPort.findUser(owner.uuid()) exists       │
│                                                                     │
│  Step 5: Circular Reference Check                               │
│  └─ Owner cannot be a member of its own owner group recursively   │
│      └── This is implicitly prevented by the data model           │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

## Validation Error Handling

### Exception Types

```java
// Base exception for all validation errors
public class CoreException extends RuntimeException {
    public CoreException(String message) { ... }
}

// Validation failure on a specific object
public class InvalidObjectException extends CoreException {
    private final Object invalidObject;
    public InvalidObjectException(String message, Object invalidObject) { ... }
}

// Entity not found
public class NotFoundException extends CoreException {
    public NotFoundException(String message) { ... }
}
```

### Error Message Format

All validation error messages should follow this pattern:

```
<fieldName> <violation> [expected format/value]
```

**Examples**:
- `fullName cannot be blank`
- `shortName must be 3-20 characters`
- `abbreviation must be exactly 3 uppercase letters`
- `technology cannot be null`
- `version must be in format X.Y.Z`
- `BusinessService with name 'HR' not found`
- `Project with shortName 'PROJ' already exists`

### Collecting Multiple Errors

For batch validation, consider implementing a validation result collector:

```java
public class ValidationResult {
    private final List<String> errors = new ArrayList<>();
    
    public boolean isValid() {
        return errors.isEmpty();
    }
    
    public void addError(String field, String message) {
        errors.add(field + " " + message);
    }
    
    public void throwIfInvalid() throws InvalidObjectException {
        if (!isValid()) {
            throw new InvalidObjectException(
                "Validation failed: " + String.join("; ", errors),
                this
            );
        }
    }
}

// Usage:
ValidationResult result = new ValidationResult();
if (StringUtils.isBlank(project.getFullName())) {
    result.addError("fullName", "cannot be blank");
}
if (project.getShortName().length() < 3) {
    result.addError("shortName", "must be at least 3 characters");
}
// ... more validations
result.throwIfInvalid();
```

## Testing Validation

Each validation chain should have corresponding unit tests:

```java
// Example: ProjectValidationTest.java

@Test
void testProjectCreation_WithValidData_PassesValidation() {
    // Given
    BusinessService bs = new BusinessService("HR", "HUM");
    UserGroup owners = new UserGroup("IT-Owners", ...);
    Project project = Project.builder()
        .fullName("Human Resources System")
        .shortName("HR-SYS")
        .description("HR management system")
        .businessService(bs)
        .owners(owners)
        .build();
    
    // When/Then
    assertDoesNotThrow(() -> project.checkIntegrity());
}

@Test
void testProjectCreation_WithBlankFullName_ThrowsException() {
    // Given
    Project project = Project.builder()
        .fullName("")
        .shortName("HR-SYS")
        .build();
    
    // When/Then
    assertThrows(InvalidObjectException.class, 
        () -> project.checkIntegrity());
}

@Test
void testProjectCreation_WithShortNameTooShort_ThrowsException() {
    // Given
    Project project = Project.builder()
        .fullName("HR System")
        .shortName("HR")  // Too short
        .build();
    
    // When/Then
    assertThrows(InvalidObjectException.class, 
        () -> project.checkIntegrity());
}
```

## See Also

- [Domain Business Logic](./core-domain-logic.md)
- [Event Management](./core-event-management.md)
- [Business Rules](./core-business-rules.md)
