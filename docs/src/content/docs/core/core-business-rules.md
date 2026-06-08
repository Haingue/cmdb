---
title: CORE Business Rules
description: Complete catalog of all domain-specific business rules and constraints in CMDB CORE
---

# CORE Business Rules

This document catalogs all domain-specific business rules implemented in the CMDB CORE module. These rules enforce the integrity, consistency, and validity of the CMDB data model.

## Rule Classification

```
┌─────────────────────────────────────────────────────────────────┐
│                    Business Rule Categories                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐   │
│  │   Entity         │  │   Relationship   │  │   State &        │   │
│  │   Integrity      │  │   Constraints    │  │   Lifecycle      │   │
│  │   Rules          │  │                  │  │   Rules          │   │
│  │                  │  │                  │  │                  │   │
│  │ - Field         │  │ - Cardinality    │  │ - State         │   │
│  │   constraints   │  │ - Ownership      │  │   transitions   │   │
│  │ - Format        │  │ - Uniqueness     │  │ - Lifecycle      │   │
│  │   validation    │  │ - Referential    │  │   management    │   │
│  │ - Business      │  │   integrity      │  │ - Archive/      │   │
│  │   logic         │  │ - Cascading      │  │   delete rules  │   │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘   │
│                                                                      │
│  ┌──────────────────┐  ┌──────────────────┐                          │
│  │   Validation     │  │   Notification    │                          │
│  │   Rules          │  │   Rules          │                          │
│  │                  │  │                  │                          │
│  │ - Uniqueness     │  │ - Update         │                          │
│  │ - Existence      │  │   required       │                          │
│  │ - Consistency    │  │ - Certificate    │                          │
│  │   checks         │  │   expiration     │                          │
│  │                  │  │ - Status         │                          │
│  │                  │  │   changes        │                          │
│  └──────────────────┘  └──────────────────┘                          │
│                                                                      │
└─────────────────────────────────────────────────────────────────┘
```

---

## Entity Integrity Rules

### Project Rules

#### Identification Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| PRJ-001 | Project must have a fullName | `fullName != null && !fullName.isBlank()` | `Project.checkIntegrity()` | `InvalidObjectException` |
| PRJ-002 | Project must have a shortName | `shortName != null && !shortName.isBlank()` | `Project.checkIntegrity()` | `InvalidObjectException` |
| PRJ-003 | Project must have a description | `description != null && !description.isBlank()` | `Project.checkIntegrity()` | `InvalidObjectException` |
| PRJ-004 | shortName must be 3-20 characters | `3 <= shortName.length() && shortName.length() <= 20` | `Project.checkIntegrity()` | `InvalidObjectException` |
| PRJ-005 | shortName must be unique | No other project with same shortName | Service layer | `ConflictException` (TBD) |
| PRJ-006 | fullName must be 3-100 characters | `3 <= fullName.length() && fullName.length() <= 100` | Service layer | `InvalidObjectException` |

#### Relationship Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| PRJ-010 | Project must have a BusinessService | `businessService != null` | `Project.checkIntegrity()` | `InvalidObjectException` |
| PRJ-011 | BusinessService must be valid | `!businessService.getName().isBlank()` | `Project.checkIntegrity()` | `InvalidObjectException` |
| PRJ-012 | BusinessService must exist | Found in database | Service layer | `NotFoundException` |
| PRJ-013 | Project must have owners | `owners != null` | `Project.checkIntegrity()` | `InvalidObjectException` |
| PRJ-014 | Owners must have at least one member | `!owners.members().isEmpty()` | Service layer | `InvalidObjectException` |
| PRJ-015 | Owners group must exist | Found in database | Service layer | `NotFoundException` |
| PRJ-016 | Maintainers is optional but if present must be valid | `maintainers == null || maintainers.checkIntegrity()` | Service layer | `InvalidObjectException` |

#### State Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| PRJ-020 | Cannot update archived project | `!project.isArchived()` | Service layer | `CoreException` |
| PRJ-021 | Archiving project archives all environments | Cascade to all environments | Service layer | N/A |
| PRJ-022 | Deleting project deletes all environments | Cascade to all environments | Service layer | N/A |

**Implementation**:
```java
// PRJ-010, PRJ-011 in Project.checkIntegrity()
public void checkIntegrity() {
    if (StringUtils.isBlank(fullName)) 
        throw new InvalidObjectException("fullName cannot be blank", this);
    if (StringUtils.isBlank(shortName)) 
        throw new InvalidObjectException("shortName cannot be blank", this);
    if (StringUtils.isBlank(description)) 
        throw new InvalidObjectException("description cannot be blank", this);
    if (businessService == null || StringUtils.isBlank(businessService.getName())) 
        throw new InvalidObjectException("businessService is missing", this);
    if (owners == null) 
        throw new InvalidObjectException("owners is missing", this);
}

// PRJ-021 in ProjectService.archive()
public void archive(UUID uuid, User initiator) {
    Project project = this.findOne(uuid, initiator);
    LocalDateTime now = LocalDateTime.now();
    project.setArchiveDatetime(now);
    project.getEnvironments().forEach(environment -> 
        this.environmentService.archive(environment.getUuid(), initiator));
    this.projectOutputPort.save(project);
}
```

---

### BusinessService Rules

#### Identification Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| BSV-001 | BusinessService must have a name | `name != null && !name.isBlank()` | `BusinessService.checkIntegrity()` | `InvalidObjectException` |
| BSV-002 | BusinessService must have an abbreviation | `abbreviation != null && !abbreviation.isBlank()` | `BusinessService.checkIntegrity()` | `InvalidObjectException` |
| BSV-003 | Abbreviation must be exactly 3 characters | `abbreviation.length() == 3` | `BusinessService.checkIntegrity()` | `InvalidObjectException` |
| BSV-004 | Abbreviation must be uppercase | `abbreviation.equals(abbreviation.toUpperCase())` | `BusinessService.checkIntegrity()` | `InvalidObjectException` |
| BSV-005 | Abbreviation must be unique | No other BS with same abbreviation | Service layer | `ConflictException` (TBD) |
| BSV-006 | Name must be unique | No other BS with same name | Service layer | `ConflictException` (TBD) |
| BSV-007 | Name must be 3-50 characters | `3 <= name.length() && name.length() <= 50` | Service layer | `InvalidObjectException` |

#### Immutability Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| BSV-010 | Abbreviation cannot be changed | Always use original abbreviation | Service layer | `CoreException` |

**Implementation**:
```java
// BSV-001, BSV-002, BSV-003, BSV-004 in BusinessService.checkIntegrity()
public void checkIntegrity() {
    if (StringUtils.isBlank(name)) 
        throw new InvalidObjectException("name cannot be blank", this);
    if (StringUtils.isBlank(abbreviation)) 
        throw new InvalidObjectException("abbreviation cannot be blank", this);
    if (abbreviation.length() != 3)
        throw new InvalidObjectException("abbreviation length must be equals to 3", this);
}

// BSV-010 in BusinessServiceService.update()
public BusinessService update(BusinessService businessService, User initiator) {
    BusinessService existing = findOne(businessService.getName(), initiator);
    // Abbreviation cannot be changed
    if (!existing.getAbbreviation().equals(businessService.getAbbreviation())) {
        throw new CoreException("BusinessService abbreviation cannot be changed");
    }
    // ... update other fields
}
```

---

### Component Rules

#### Identification Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| CMP-001 | Component must have a name | `name != null && !name.isBlank()` | `Component.checkIntegrity()` | `InvalidObjectException` |
| CMP-002 | Name must be 3-100 characters | `3 <= name.length() && name.length() <= 100` | Service layer | `InvalidObjectException` |
| CMP-003 | Name must be unique within environment | No other component with same name in environment | Service layer | `ConflictException` (TBD) |

#### Type Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| CMP-010 | Component must have a type | `type != null` | `Component.checkIntegrity()` | `InvalidObjectException` |
| CMP-011 | Type is immutable | Cannot change after creation | Service layer | `CoreException` |
| CMP-012 | Type must be valid ComponentType | Valid enum value | `Component.checkIntegrity()` | `InvalidObjectException` |

#### Technology & Version Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| CMP-020 | Component must have a technology | `technology != null` | `Component.checkIntegrity()` | `InvalidObjectException` |
| CMP-021 | Technology must exist | Found in catalog | Service layer | `NotFoundException` |
| CMP-022 | Component must have a version | `version != null` | `Component.checkIntegrity()` | `InvalidObjectException` |
| CMP-023 | Version must be valid format | `version.toString().matches("[0-9]+\.[0-9]+\.[0-9]+")` | Service layer | `InvalidObjectException` |
| CMP-024 | Version must be compatible with technology | `version >= technology.minimalVersion` (if defined) | Service layer | `InvalidObjectException` |

#### Update Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| CMP-030 | Component needs update if below minimal version | `technology.needsUpdate(version)` | `Component.needsUpdate()` | N/A (returns boolean) |
| CMP-031 | Certificate must be valid if present | Certificate not expired, valid format | Service layer | `InvalidObjectException` |

**Implementation**:
```java
// CMP-001, CMP-010, CMP-020, CMP-022 in Component.checkIntegrity()
public void checkIntegrity() {
    if(StringUtils.isBlank(name)) 
        throw new InvalidObjectException("name cannot be blank", this);
    if(type == null) 
        throw new InvalidObjectException("type cannot be null", this);
    if(version == null) 
        throw new InvalidObjectException("version cannot be null", this);
    if(technology == null) 
        throw new InvalidObjectException("technology cannot be null", this);
}

// CMP-030 in Component.needsUpdate()
public boolean needsUpdate() {
    return technology.needsUpdate(version);
}

// CMP-024 in ComponentService.create()
public Component create(Component component, User initiator) {
    // ...
    if (component.getTechnology().getMinimalVersion() != null &&
        component.getVersion().compareTo(component.getTechnology().getMinimalVersion()) < 0) {
        throw new InvalidObjectException(
            "version " + component.getVersion() + " is below minimal version " + 
            component.getTechnology().getMinimalVersion(), 
            component);
    }
    // ...
}
```

#### Type-Specific Rules

**Host**:
| Rule ID | Rule | Constraint |
|---------|------|------------|
| HOST-001 | If hostname specified, must be valid format | Valid hostname pattern |
| HOST-002 | If IP address specified, must be valid | Valid IPv4/IPv6 format |
| HOST-003 | OS must be specified | `os != null && !os.isBlank()` |

**VirtualMachine**:
| Rule ID | Rule | Constraint |
|---------|------|------------|
| VM-001 | Hypervisor must be specified | `hypervisor != null && !hypervisor.isBlank()` |
| VM-002 | VM ID must be unique within hypervisor | No duplicate vmId per hypervisor |
| VM-003 | Must have valid hostname | Valid hostname format |

**Hardware**:
| Rule ID | Rule | Constraint |
|---------|------|------------|
| HW-001 | Serial number must be specified | `serialNumber != null && !serialNumber.isBlank()` |
| HW-002 | Serial number must be 5-50 characters | `5 <= serialNumber.length() && serialNumber.length() <= 50` |
| HW-003 | Manufacturer must be specified | `manufacturer != null && !manufacturer.isBlank()` |
| HW-004 | Model must be specified | `model != null && !model.isBlank()` |

**Software**:
| Rule ID | Rule | Constraint |
|---------|------|------------|
| SW-001 | Package name must be specified | `packageName != null && !packageName.isBlank()` |
| SW-002 | Vendor must be specified | `vendor != null && !vendor.isBlank()` |

---

### Environment Rules

#### Identification Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| ENV-001 | Environment must have a name | `name != null && !name.isBlank()` | Service layer | `InvalidObjectException` |
| ENV-002 | Name must be 3-50 characters | `3 <= name.length() && name.length() <= 50` | Service layer | `InvalidObjectException` |
| ENV-003 | Name must be unique within project | No other environment with same name in project | Service layer | `ConflictException` (TBD) |

#### Location Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| ENV-010 | Environment must have a location | `location != null && !location.isBlank()` | Service layer | `InvalidObjectException` |
| ENV-011 | Location must be 2-100 characters | `2 <= location.length() && location.length() <= 100` | Service layer | `InvalidObjectException` |

#### Type & Status Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| ENV-020 | Environment must have a type | `type != null` | Service layer | `InvalidObjectException` |
| ENV-021 | Type must be valid EnvironmentType | Valid enum value | Service layer | `InvalidObjectException` |
| ENV-022 | Environment must have a status | `status != null` | Service layer | `InvalidObjectException` |
| ENV-023 | Status must be valid EnvironmentStatus | Valid enum value | Service layer | `InvalidObjectException` |

#### Network Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| ENV-030 | Network area must be valid | Valid NetworkArea enum value | Service layer | `InvalidObjectException` |

#### JIRA Integration Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| ENV-040 | JIRA tracker is optional | Can be null | N/A | N/A |
| ENV-041 | If JIRA tracker specified, must be valid format | Pattern: `^[A-Z]+-\d+$` | Service layer | `InvalidObjectException` |

#### Relationship Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| ENV-050 | Environment must belong to a project | `project != null` | Service layer | `InvalidObjectException` |
| ENV-051 | Project must exist | Found in database | Service layer | `NotFoundException` |
| ENV-052 | Project must not be archived | `!project.isArchived()` | Service layer | `InvalidObjectException` |

**Implementation**:
```java
// ENV-021, ENV-023 validation
public void validateEnvironmentTypeAndStatus(Environment environment) {
    if (environment.getType() == null) {
        throw new InvalidObjectException("Environment type cannot be null", environment);
    }
    if (environment.getStatus() == null) {
        throw new InvalidObjectException("Environment status cannot be null", environment);
    }
}

// ENV-041 validation
public void validateJiraTracker(String jiraTracker) {
    if (jiraTracker != null && !jiraTracker.matches("^[A-Z]+-\\d+$")) {
        throw new InvalidObjectException(
            "jiraTracker must match pattern [A-Z]+-\\d+", 
            jiraTracker);
    }
}
```

---

### Technology Rules

#### Identification Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| TEC-001 | Technology must have a name | `name != null && !name.isBlank()` | Service layer | `InvalidObjectException` |
| TEC-002 | Name must be 3-50 characters | `3 <= name.length() && name.length() <= 50` | Service layer | `InvalidObjectException` |
| TEC-003 | Name must be unique | No other technology with same name | Service layer | `ConflictException` (TBD) |

#### Classification Rules

| Rule ID | Rule | Constraint | Validation Location | Validation Type |
|---------|------|------------|---------------------|----------------|
| TEC-010 | Technology must have a type | `type != null` | Service layer | `InvalidObjectException` |
| TEC-011 | Type must be valid TechnologyType | Valid enum value | Service layer | `InvalidObjectException` |

#### Version Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| TEC-020 | minimalVersion is optional | Can be null | N/A | N/A |
| TEC-021 | If minimalVersion specified, must be valid | Valid Version format | Service layer | `InvalidObjectException` |
| TEC-022 | targetVersion is optional | Can be null | N/A | N/A |
| TEC-023 | If targetVersion specified, must be valid | Valid Version format | Service layer | `InvalidObjectException` |
| TEC-024 | lastVersion is optional | Can be null | N/A | N/A |
| TEC-025 | If lastVersion specified, must be valid | Valid Version format | Service layer | `InvalidObjectException` |

#### Version Relationship Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| TEC-030 | minimalVersion <= targetVersion (if both present) | `minimalVersion.compareTo(targetVersion) <= 0` | Service layer | `InvalidObjectException` |
| TEC-031 | minimalVersion <= lastVersion (if both present) | `minimalVersion.compareTo(lastVersion) <= 0` | Service layer | `InvalidObjectException` |
| TEC-032 | targetVersion <= lastVersion (if both present) | `targetVersion.compareTo(lastVersion) <= 0` | Service layer | `InvalidObjectException` |

#### Programming Language Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| TEC-040 | programmingLanguage is optional | Can be null | N/A | N/A |

**Implementation**:
```java
// TEC-030, TEC-031, TEC-032 validation in TechnologyService
public void validateVersionRelationships(Technology technology) {
    if (technology.getMinimalVersion() != null && 
        technology.getTargetVersion() != null &&
        technology.getMinimalVersion().compareTo(technology.getTargetVersion()) > 0) {
        throw new InvalidObjectException(
            "minimalVersion cannot be newer than targetVersion", 
            technology);
    }
    if (technology.getMinimalVersion() != null && 
        technology.getLastVersion() != null &&
        technology.getMinimalVersion().compareTo(technology.getLastVersion()) > 0) {
        throw new InvalidObjectException(
            "minimalVersion cannot be newer than lastVersion", 
            technology);
    }
    if (technology.getTargetVersion() != null && 
        technology.getLastVersion() != null &&
        technology.getTargetVersion().compareTo(technology.getLastVersion()) > 0) {
        throw new InvalidObjectException(
            "targetVersion cannot be newer than lastVersion", 
            technology);
    }
}

// TEC-021, TEC-023, TEC-025 validation
public void validateVersions(Technology technology) {
    if (technology.getMinimalVersion() != null && 
        !isValidVersion(technology.getMinimalVersion())) {
        throw new InvalidObjectException(
            "minimalVersion format is invalid", 
            technology);
    }
    // Similar for targetVersion and lastVersion
}

private boolean isValidVersion(Version version) {
    try {
        version.toString();  // Will throw if invalid
        return true;
    } catch (Exception e) {
        return false;
    }
}
```

---

### UserGroup Rules

#### Identification Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| UGR-001 | UserGroup must have a name | `name != null && !name.isBlank()` | `UserGroup.checkIntegrity()` | `InvalidObjectException` |
| UGR-002 | Name must be 3-50 characters | `3 <= name.length() && name.length() <= 50` | Service layer | `InvalidObjectException` |
| UGR-003 | Name must be unique | No other group with same name | Service layer | `ConflictException` (TBD) |

#### Contact Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| UGR-010 | Email is optional | Can be null | N/A | N/A |
| UGR-011 | If email specified, must be valid | Valid email format | `UserGroup.checkIntegrity()` | `InvalidObjectException` |

#### Ownership Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| UGR-020 | UserGroup must have an owner | `owner != null` | `UserGroup.checkIntegrity()` | `InvalidObjectException` |
| UGR-021 | Owner must be a valid User | `owner.uuid() != null` | Service layer | `InvalidObjectException` |
| UGR-022 | Owner user must exist | Found in database | Service layer | `NotFoundException` |

#### Membership Rules

| Rule ID | Rule | Constraint | Validation Location | Exception Type |
|---------|------|------------|---------------------|----------------|
| UGR-030 | UserGroup must have members | `members != null && !members.isEmpty()` | `UserGroup.checkIntegrity()` | `InvalidObjectException` |
| UGR-031 | All members must be valid Users | Each `member.uuid() != null` | Service layer | `InvalidObjectException` |
| UGR-032 | All member users must exist | Found in database | Service layer | `NotFoundException` |
| UGR-033 | Owner is implicitly a member | By construction | N/A | N/A |

**Implementation**:
```java
// UGR-001, UGR-010, UGR-020, UGR-030 in UserGroup.checkIntegrity()
public boolean checkIntegrity() throws InvalidObjectException {
    if (StringUtils.isBlank(this.name())) 
        throw new InvalidObjectException("name is blank", this);
    if (this.owner() == null) 
        throw new InvalidObjectException("owner is blank", this);
    if (this.members() == null || this.members.isEmpty()) 
        throw new InvalidObjectException("there is no member", this);
    return true;
}
```

---

## Relationship Rules

### Cardinality Rules

| Rule ID | Relationship | Cardinality | Constraint | Validation Location |
|---------|--------------|-------------|------------|---------------------|
| REL-001 | Project ↔ BusinessService | Many-to-Many | Project has 1-N BusinessServices | Service layer |
| REL-002 | Project ↔ Environment | One-to-Many | Project has 0-N Environments | N/A |
| REL-003 | Environment ↔ Component | One-to-Many | Environment has 0-N Components | N/A |
| REL-004 | Component ↔ Technology | Many-to-One | Component has exactly 1 Technology | `Component.checkIntegrity()` |
| REL-005 | Component ↔ Version | Many-to-One | Component has exactly 1 Version | `Component.checkIntegrity()` |
| REL-006 | Project ↔ UserGroup (owners) | Many-to-One | Project has exactly 1 owners group | `Project.checkIntegrity()` |
| REL-007 | Project ↔ UserGroup (maintainers) | Many-to-One | Project has 0-1 maintainers group | Service layer |
| REL-008 | UserGroup ↔ User (owner) | Many-to-One | UserGroup has exactly 1 owner | `UserGroup.checkIntegrity()` |
| REL-009 | UserGroup ↔ User (members) | Many-to-Many | UserGroup has 1-N members | `UserGroup.checkIntegrity()` |

### Ownership Rules

| Rule ID | Rule | Constraint | Validation Location |
|---------|------|------------|---------------------|
| REL-010 | Project owns its Environments | Environments cannot exist without Project | Service layer |
| REL-011 | Environment owns its Components | Components cannot exist without Environment | Service layer |
| REL-012 | UserGroup owns its members | Members are managed by group owner | Service layer |

### Cascading Rules

| Rule ID | Rule | Constraint | Implementation |
|---------|------|------------|----------------|
| CAS-001 | Archiving Project archives all Environments | Automatic cascade | `ProjectService.archive()` |
| CAS-002 | Deleting Project deletes all Environments | Automatic cascade | `ProjectService.delete()` |
| CAS-003 | Archiving Environment archives all Components | Automatic cascade | `EnvironmentService.archive()` |
| CAS-004 | Deleting Environment deletes all Components | Automatic cascade | `EnvironmentService.delete()` |

**Implementation**:
```java
// CAS-001 in ProjectService.archive()
public void archive(UUID uuid, User initiator) {
    Project project = this.findOne(uuid, initiator);
    LocalDateTime now = LocalDateTime.now();
    project.setArchiveDatetime(now);
    
    // Archive all environments
    project.getEnvironments().forEach(environment -> 
        this.environmentService.archive(environment.getUuid(), initiator)
    );
    
    this.projectOutputPort.save(project);
}

// CAS-002 in ProjectService.delete()
public void delete(UUID uuid, User initiator) {
    if (uuid == null) throw new CoreException("Project uuid cannot be null");
    
    Project existingProject = this.findOne(uuid, initiator);
    
    // Delete all environments
    existingProject.getEnvironments().stream()
        .peek(environment -> this.environmentService.delete(environment.getUuid(), initiator))
        .forEach(existingProject::removeEnvironment);
    
    existingProject.archive(GlobalStaticParameter.SYSTEM_NAME.name());
    projectOutputPort.save(existingProject);
}
```

### Uniqueness Rules

| Rule ID | Rule | Constraint | Validation Location |
|---------|------|------------|---------------------|
| UNI-001 | Project.shortName must be unique | Across all projects | Service layer |
| UNI-002 | BusinessService.name must be unique | Across all business services | Service layer |
| UNI-003 | BusinessService.abbreviation must be unique | Across all business services | Service layer |
| UNI-004 | Technology.name must be unique | Across all technologies | Service layer |
| UNI-005 | UserGroup.name must be unique | Across all user groups | Service layer |
| UNI-006 | Component.name must be unique within Environment | Per environment | Service layer |
| UNI-007 | Environment.name must be unique within Project | Per project | Service layer |

---

## State & Lifecycle Rules

### Project Lifecycle

```
┌─────────────────────────────────────────────────────────────┐
│                    PROJECT LIFECYCLE                           │
├─────────────────────────────────────────────────────────────┤
│                                                                  │
│  CREATE (Initial State)                                         │
│       │                                                         │
│       ▼                                                         │
│  ACTIVE (Normal State)                                          │
│       │                                                         │
│       ├── Can be updated                                        │
│       ├── Can have environments added/removed                 │
│       └── Can be archived or deleted                            │
│       │                                                         │
│       ▼                                                         │
│  ARCHIVED (Soft Deleted)                                        │
│       │                                                         │
│       ├── Cannot be updated                                     │
│       ├── Cannot have environments added                       │
│       └── Can be permanently deleted                            │
│       │                                                         │
│       ▼                                                         │
│  DELETED (Permanently Removed) - Terminal State                │
│                                                                  │
└─────────────────────────────────────────────────────────────┘
```

**Rules**:
- LFC-PRJ-001: Project starts in ACTIVE state after creation
- LFC-PRJ-002: ACTIVE project can be updated
- LFC-PRJ-003: ACTIVE project can be archived
- LFC-PRJ-004: ACTIVE project can be deleted
- LFC-PRJ-005: ARCHIVED project cannot be updated
- LFC-PRJ-006: ARCHIVED project can be deleted
- LFC-PRJ-007: DELETED project cannot be modified (doesn't exist)

### Environment Lifecycle

```
┌─────────────────────────────────────────────────────────────┐
│                  ENVIRONMENT LIFECYCLE                           │
├─────────────────────────────────────────────────────────────┤
│                                                                  │
│  PLANNED (Initial State)                                       │
│       │                                                         │
│       ▼                                                         │
│  CREATING (Setup in Progress)                                   │
│       │                                                         │
│       ├── Can transition back to PLANNED                        │
│       └── Can transition to ACTIVE                              │
│       │                                                         │
│       ▼                                                         │
│  ACTIVE (Operational)                                           │
│       │                                                         │
│       ├── Can transition to MAINTENANCE                         │
│       └── Can transition to DECOMMISSIONED                      │
│       │                                                         │
│       ▼                                                         │
│  MAINTENANCE (Temporary Unavailable)                            │
│       │                                                         │
│       ├── Can transition back to ACTIVE                         │
│       └── Can transition to DECOMMISSIONED                      │
│       │                                                         │
│       ▼                                                         │
│  DECOMMISSIONED (Permanently Retired) - Terminal State         │
│                                                                  │
└─────────────────────────────────────────────────────────────┘
```

**Rules**:
- LFC-ENV-001: Environment starts in PLANNED state
- LFC-ENV-002: PLANNED can transition to CREATING
- LFC-ENV-003: PLANNED can remain PLANNED
- LFC-ENV-004: CREATING can transition to PLANNED
- LFC-ENV-005: CREATING can transition to ACTIVE
- LFC-ENV-006: ACTIVE can transition to MAINTENANCE
- LFC-ENV-007: ACTIVE can transition to DECOMMISSIONED
- LFC-ENV-008: MAINTENANCE can transition to ACTIVE
- LFC-ENV-009: MAINTENANCE can transition to DECOMMISSIONED
- LFC-ENV-010: DECOMMISSIONED is terminal (no outgoing transitions)

**Implementation**:
```java
// In EnvironmentService.update()
public Environment update(Environment environment, User initiator) {
    Environment existing = findOne(environment.getUuid(), initiator);
    EnvironmentStatus oldStatus = existing.getStatus();
    EnvironmentStatus newStatus = environment.getStatus();
    
    // Validate state transition
    if (!isValidStateTransition(oldStatus, newStatus)) {
        throw new CoreException(
            "Cannot transition from " + oldStatus + " to " + newStatus);
    }
    
    // ... update other fields
    
    return environmentOutputPort.save(existing);
}

private boolean isValidStateTransition(EnvironmentStatus from, EnvironmentStatus to) {
    return switch (from) {
        case PLANNED -> to == CREATING || to == PLANNED;
        case CREATING -> to == ACTIVE || to == PLANNED;
        case ACTIVE -> to == MAINTENANCE || to == DECOMMISSIONED;
        case MAINTENANCE -> to == ACTIVE || to == DECOMMISSIONED;
        case DECOMMISSIONED -> false;  // Terminal state
    };
}
```

### Component Lifecycle

```
┌─────────────────────────────────────────────────────────────┐
│                   COMPONENT LIFECYCLE                            │
├─────────────────────────────────────────────────────────────┤
│                                                                  │
│  ACTIVE (Normal State)                                          │
│       │                                                         │
│       ├── Can be updated                                        │
│       ├── Can be archived                                       │
│       └── Can be deleted                                         │
│       │                                                         │
│       ▼                                                         │
│  ARCHIVED (Soft Deleted)                                        │
│       │                                                         │
│       └── Can be permanently deleted                             │
│       │                                                         │
│       ▼                                                         │
│  DELETED (Permanently Removed) - Terminal State                │
│                                                                  │
└─────────────────────────────────────────────────────────────┘
```

**Rules**:
- LFC-CMP-001: Component starts in ACTIVE state after creation
- LFC-CMP-002: ACTIVE component can be updated
- LFC-CMP-003: ACTIVE component can be archived
- LFC-CMP-004: ACTIVE component can be deleted
- LFC-CMP-005: ARCHIVED component cannot be updated
- LFC-CMP-006: ARCHIVED component can be deleted
- LFC-CMP-007: DELETED component cannot be modified

---

## Validation Rules

### Uniqueness Validation

```java
// Generic uniqueness check pattern
public boolean existsByField(Class<?> entityClass, String fieldName, Object value) {
    // Implementation depends on repository
    // Query: SELECT COUNT(*) FROM entityClass WHERE fieldName = value
    return repository.countByField(entityClass, fieldName, value) > 0;
}

// Specific implementations:
public boolean projectShortNameExists(String shortName) {
    return projectOutputPort.existsByShortName(shortName);
}

public boolean businessServiceNameExists(String name) {
    return businessServiceOutputPort.existsByName(name);
}

public boolean businessServiceAbbreviationExists(String abbreviation) {
    return businessServiceOutputPort.existsByAbbreviation(abbreviation);
}
```

### Referential Integrity Validation

```java
// Check if referenced entity exists
public void validateReferenceExists(UUID uuid, Class<?> entityClass, 
                                      OutputPort<?, UUID> outputPort) {
    if (uuid != null && !outputPort.existsById(uuid)) {
        throw new NotFoundException(entityClass.getSimpleName() + 
                                  " with id " + uuid + " not found");
    }
}

// Usage:
validateReferenceExists(
    project.getBusinessService().getUuid(),
    BusinessService.class,
    businessServiceOutputPort
);
```

---

## Notification Rules

### Update Required Notifications

| Rule ID | Rule | Trigger | Recipients | Priority |
|---------|------|---------|------------|----------|
| NOT-001 | Component version below minimal | `component.needsUpdate()` | Project maintainers, Component owner | HIGH |
| NOT-002 | Certificate expiration warning | Certificate expires in < 30 days | Component maintainers | HIGH |
| NOT-003 | Certificate expired | Certificate is expired | Component maintainers, Project owners | CRITICAL |

### Ownership Change Notifications

| Rule ID | Rule | Trigger | Recipients | Priority |
|---------|------|---------|------------|----------|
| NOT-010 | Project owners changed | owners field modified | Old owners, New owners | MEDIUM |
| NOT-011 | Project maintainers changed | maintainers field modified | Old maintainers, New maintainers | MEDIUM |
| NOT-012 | Component owner changed | Not yet implemented | Old owner, New owner | MEDIUM |

### Status Change Notifications

| Rule ID | Rule | Trigger | Recipients | Priority |
|---------|------|---------|------------|----------|
| NOT-020 | Environment status to PRODUCTION | status = PRODUCTION | Project owners, Project maintainers | HIGH |
| NOT-021 | Environment status to DECOMMISSIONED | status = DECOMMISSIONED | Project owners | MEDIUM |
| NOT-022 | Project archived | archive operation | Project owners, Project maintainers | MEDIUM |

### Creation Notifications

| Rule ID | Rule | Trigger | Recipients | Priority |
|---------|------|---------|------------|----------|
| NOT-030 | Project created | create operation | Project owners | LOW |
| NOT-031 | Component created | create operation | Project maintainers | LOW |

**Implementation Notes**:
- Notification logic should be in the service layer
- Actual notification delivery (email, Slack, etc.) is handled by infrastructure services
- CORE should identify what needs to be notified, not how to notify

```java
// Example: Check for update required notification
public void checkForUpdateNotifications(Component component, User initiator) {
    if (component.needsUpdate()) {
        // Trigger notification
        notificationService.notifyUpdateRequired(
            component,
            component.getMaintainers(),  // Recipients
            initiator
        );
    }
}

// In ComponentService.update()
public Component update(Component component, User initiator) {
    Component existing = findOne(component.getUuid(), initiator);
    
    // ... update logic
    
    // Check if version changed and needs update
    if (!existing.getVersion().equals(component.getVersion()) && 
        component.needsUpdate()) {
        checkForUpdateNotifications(component, initiator);
    }
    
    return componentOutputPort.save(component);
}
```

---

## Rule Summary by Entity

### Project: 22 Rules
- Identification: 6 rules (PRJ-001 to PRJ-006)
- Relationship: 7 rules (PRJ-010 to PRJ-016)
- State: 3 rules (PRJ-020 to PRJ-022)
- Lifecycle: 7 rules (LFC-PRJ-001 to LFC-PRJ-007)

### BusinessService: 11 Rules
- Identification: 7 rules (BSV-001 to BSV-007)
- Immutability: 1 rule (BSV-010)
- Lifecycle: Implicit (similar to Project)

### Component: 20+ Rules
- Identification: 3 rules (CMP-001 to CMP-003)
- Type: 3 rules (CMP-010 to CMP-012)
- Technology & Version: 5 rules (CMP-020 to CMP-024)
- Update: 2 rules (CMP-030 to CMP-031)
- Type-specific: 8+ rules
- Lifecycle: 7 rules (LFC-CMP-001 to LFC-CMP-007)

### Environment: 17 Rules
- Identification: 3 rules (ENV-001 to ENV-003)
- Location: 2 rules (ENV-010 to ENV-011)
- Type & Status: 4 rules (ENV-020 to ENV-023)
- Network: 1 rule (ENV-030)
- JIRA: 2 rules (ENV-040 to ENV-041)
- Relationship: 3 rules (ENV-050 to ENV-052)
- Lifecycle: 10 rules (LFC-ENV-001 to LFC-ENV-010)

### Technology: 13 Rules
- Identification: 3 rules (TEC-001 to TEC-003)
- Classification: 2 rules (TEC-010 to TEC-011)
- Version: 5 rules (TEC-020 to TEC-025)
- Version Relationship: 3 rules (TEC-030 to TEC-032)
- Programming Language: 1 rule (TEC-040)

### UserGroup: 10 Rules
- Identification: 3 rules (UGR-001 to UGR-003)
- Contact: 2 rules (UGR-010 to UGR-011)
- Ownership: 3 rules (UGR-020 to UGR-022)
- Membership: 4 rules (UGR-030 to UGR-033)

### Cross-Entity: 20+ Rules
- Relationship: 9 rules (REL-001 to REL-012)
- Cascading: 4 rules (CAS-001 to CAS-004)
- Uniqueness: 7 rules (UNI-001 to UNI-007)
- Notification: 10+ rules (NOT-001 to NOT-031)
- State & Lifecycle: 20+ rules

---

## Implementation Status

### Implemented ✅
- [x] PRJ-001 to PRJ-004: Project field validation in `checkIntegrity()`
- [x] PRJ-010, PRJ-011: BusinessService validation in `checkIntegrity()`
- [x] PRJ-013: Owners validation in `checkIntegrity()`
- [x] BSV-001, BSV-002, BSV-003: BusinessService validation in `checkIntegrity()`
- [x] CMP-001, CMP-010, CMP-020, CMP-022: Component validation in `checkIntegrity()`
- [x] CMP-030: needsUpdate() logic
- [x] UGR-001, UGR-020, UGR-030: UserGroup validation in `checkIntegrity()`
- [x] CAS-001: Project archive cascades to environments
- [x] Event tracking for all state changes

### To Be Implemented ⚠️
- [ ] PRJ-005: shortName uniqueness check
- [ ] BSV-005, BSV-006: BusinessService uniqueness checks
- [ ] CMP-002: Component name length validation
- [ ] CMP-003: Component name uniqueness within environment
- [ ] ENV-001 to ENV-041: Environment validation
- [ ] TEC-001 to TEC-040: Technology validation
- [ ] UGR-011: UserGroup email validation
- [ ] UGR-021, UGR-022: UserGroup owner existence check
- [ ] UGR-031, UGR-032: UserGroup member validation
- [ ] CAS-002, CAS-003, CAS-004: Delete cascading
- [ ] All uniqueness checks
- [ ] All state transition validations
- [ ] All notification triggers
- [ ] Permission validation (marked as TODO in services)

---

## Testing Business Rules

Each business rule should have corresponding unit tests:

```java
// Example: Project Business Rules Tests

@Test
void testProject_CreationWithoutFullName_ThrowsException() {
    Project project = Project.builder()
        .shortName("TEST")
        .description("Test")
        .build();
    
    assertThrows(InvalidObjectException.class, project::checkIntegrity);
}

@Test
void testProject_CreationWithoutBusinessService_ThrowsException() {
    Project project = Project.builder()
        .fullName("Test")
        .shortName("TEST")
        .description("Test")
        .build();
    
    assertThrows(InvalidObjectException.class, project::checkIntegrity);
}

@Test
void testBusinessService_CreationWithInvalidAbbreviationLength_ThrowsException() {
    BusinessService bs = new BusinessService("Test", "AB");  // Too short
    
    assertThrows(InvalidObjectException.class, bs::checkIntegrity);
}

@Test
void testComponent_NeedsUpdate_WhenVersionBelowMinimal_ReturnsTrue() {
    Technology tech = Technology.builder()
        .name("Java")
        .minimalVersion(new Version(11, 0, 0))
        .build();
    
    Component component = new Host();
    component.setVersion(new Version(8, 0, 0));
    component.setTechnology(tech);
    
    assertTrue(component.needsUpdate());
}

@Test
void testComponent_NeedsUpdate_WhenVersionAboveMinimal_ReturnsFalse() {
    Technology tech = Technology.builder()
        .name("Java")
        .minimalVersion(new Version(11, 0, 0))
        .build();
    
    Component component = new Host();
    component.setVersion(new Version(17, 0, 0));
    component.setTechnology(tech);
    
    assertFalse(component.needsUpdate());
}
```

---

## See Also

- [Domain Business Logic](./core-domain-logic.md) - Complete overview of business logic
- [Validation Chains](./core-validation-chains.md) - Detailed validation workflows
- [Event Management](./core-event-management.md) - Event tracking and audit trails
- [Architecture Overview](../architecture.md) - System architecture
