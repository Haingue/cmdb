---
title: CORE Implementation Guide
description: Step-by-step guide to implementing the CORE domain business logic, validation chains, and event management
---

# CORE Implementation Guide

This guide provides a step-by-step approach to implementing the business logic documented in the CORE module. It connects the documentation to actual code implementation.

## Implementation Roadmap

### Phase 1: Complete Existing Validation (High Priority)

**Goal**: Ensure all entities have proper `checkIntegrity()` methods and that existing validation is consistent.

#### Tasks:

1. **Review and enhance existing `checkIntegrity()` methods**
   - [ ] `Project.checkIntegrity()` - Add length validation for fields
   - [ ] `BusinessService.checkIntegrity()` - Add uppercase check for abbreviation
   - [ ] `Component.checkIntegrity()` - Add all required field checks
   - [ ] `UserGroup.checkIntegrity()` - Add email validation

2. **Add missing `checkIntegrity()` methods**
   - [ ] `Environment` - Needs comprehensive validation
   - [ ] `Technology` - Needs field validation
   - [ ] `Version` - Already has implicit validation via `fromString()`

**Estimated effort**: 2-4 hours

### Phase 2: Implement Uniqueness Validation (High Priority)

**Goal**: Prevent duplicate entities in the system.

#### New Exceptions to Create:
```java
// ConflictException.java
package com.management.cmdb.core.models.exceptions;

public class ConflictException extends CoreException {
    public ConflictException(String message) {
        super(message);
    }
}
```

#### Service Layer Updates:

1. **ProjectService**
   ```java
   public Project create(Project project, User initiator) {
       // Check uniqueness
       if (projectOutputPort.existsByShortName(project.getShortName())) {
           throw new ConflictException(
               "Project with shortName '" + project.getShortName() + "' already exists");
       }
       // ... rest of create logic
   }
   ```

2. **BusinessServiceService**
   ```java
   public BusinessService create(BusinessService bs, User initiator) {
       if (businessServiceOutputPort.existsByName(bs.getName())) {
           throw new ConflictException("BusinessService with name '" + bs.getName() + "' already exists");
       }
       if (businessServiceOutputPort.existsByAbbreviation(bs.getAbbreviation())) {
           throw new ConflictException("BusinessService with abbreviation '" + bs.getAbbreviation() + "' already exists");
       }
       // ... rest of create logic
   }
   ```

3. **ComponentService, EnvironmentService, TechnologyService, IdentityService**
   - Add similar uniqueness checks

**Estimated effort**: 4-6 hours

### Phase 3: Enhance Event Management (Medium Priority)

**Goal**: Improve event descriptions and ensure consistent event creation.

#### Tasks:

1. **Create EventDescriptionBuilder utility**
   - See [Event Management](./core-event-management.md#description-building-helper)
   - Location: `core/src/main/java/com/management/cmdb/core/utils/EventDescriptionBuilder.java`

2. **Update all services to use descriptive events**
   - Add meaningful descriptions to all CREATE/UPDATE/ARCHIVE events
   - Include field changes in UPDATE events

3. **Ensure all state changes create events**
   - Audit all service methods for missing event creation
   - Verify revision is incremented for every change

**Estimated effort**: 3-5 hours

### Phase 4: Implement Cross-Entity Validation (High Priority)

**Goal**: Validate relationships and references between entities.

#### Tasks:

1. **Add reference validation methods**
   ```java
   // In service layer
   private void validateBusinessServiceExists(BusinessService bs, User initiator) {
       if (bs != null && bs.getName() != null) {
           businessServiceService.findOne(bs.getName(), initiator);
       }
   }
   
   private void validateUserGroupExists(UserGroup group, User initiator) {
       if (group != null && group.name() != null) {
           identityService.findUserGroup(group.name(), initiator);
       }
   }
   ```

2. **Update ProjectService.create()**
   ```java
   public Project create(Project project, User initiator) {
       project.checkIntegrity();
       
       // Validate references
       validateBusinessServiceExists(project.getBusinessService(), initiator);
       validateUserGroupExists(project.getOwners(), initiator);
       if (project.getMaintainers() != null) {
           validateUserGroupExists(project.getMaintainers(), initiator);
       }
       
       // ... rest of create logic
   }
   ```

3. **Add similar validation to all update methods**

**Estimated effort**: 4-6 hours

### Phase 5: Implement State Transition Validation (Medium Priority)

**Goal**: Ensure only valid state transitions are allowed.

#### Tasks:

1. **Create state transition validation for Environment**
   ```java
   // In EnvironmentService
   private boolean isValidStateTransition(EnvironmentStatus from, EnvironmentStatus to) {
       return switch (from) {
           case PLANNED -> to == CREATING || to == PLANNED;
           case CREATING -> to == ACTIVE || to == PLANNED;
           case ACTIVE -> to == MAINTENANCE || to == DECOMMISSIONED;
           case MAINTENANCE -> to == ACTIVE || to == DECOMMISSIONED;
           case DECOMMISSIONED -> false;  // Terminal state
       };
   }
   
   public Environment update(Environment environment, User initiator) {
       Environment existing = findOne(environment.getUuid(), initiator);
       
       if (existing.getStatus() != environment.getStatus()) {
           if (!isValidStateTransition(existing.getStatus(), environment.getStatus())) {
               throw new CoreException(
                   "Cannot transition Environment from " + existing.getStatus() + 
                   " to " + environment.getStatus());
           }
       }
       
       // ... rest of update logic
   }
   ```

2. **Add state transition validation for other entities if needed**

**Estimated effort**: 2-3 hours

### Phase 6: Implement Cascading Operations (Medium Priority)

**Goal**: Complete all cascading delete/archive operations.

#### Tasks:

1. **Complete ProjectService.delete()**
   ```java
   @Override
   public void delete(UUID uuid, User initiator) {
       if (uuid == null) throw new CoreException("Project uuid cannot be null");
       
       Project existingProject = this.findOne(uuid, initiator);
       
       // Delete all environments and their components
       existingProject.getEnvironments().forEach(environment -> {
           // Archive all components first
           environment.getComponents().forEach(component -> 
               componentService.delete(component.getUuid(), initiator));
           // Then delete environment
           environmentService.delete(environment.getUuid(), initiator);
       });
       
       // Archive the project
       existingProject.archive(initiator.uuid().toString());
       projectOutputPort.save(existingProject);
   }
   ```

2. **Complete EnvironmentService.delete()**
   - Delete all components in environment
   - Then delete environment

3. **Add delete methods to ComponentService**

**Estimated effort**: 3-4 hours

### Phase 7: Implement Notification Triggers (Low Priority)

**Goal**: Add notification identification for important events.

#### Tasks:

1. **Create NotificationTrigger enum**
   ```java
   package com.management.cmdb.core.models.technical;
   
   public enum NotificationTrigger {
       UPDATE_REQUIRED,       // Component version below minimal
       CERTIFICATE_EXPIRING,   // Certificate expires in < 30 days
       CERTIFICATE_EXPIRED,    // Certificate is expired
       OWNERS_CHANGED,        // Project owners changed
       MAINTAINERS_CHANGED,   // Project maintainers changed
       STATUS_TO_PRODUCTION,  // Environment status changed to PROD
       ENTITY_ARCHIVED,        // Any entity archived
       ENTITY_CREATED         // Any entity created
   }
   ```

2. **Create NotificationRequest model**
   ```java
   package com.management.cmdb.core.models.business.request;
   
   import com.management.cmdb.core.models.technical.NotificationTrigger;
   import com.management.cmdb.core.models.business.identity.User;
   import java.util.Set;
   
   public record NotificationRequest(
       NotificationTrigger trigger,
       String entityType,
       UUID entityUuid,
       String description,
       Set<User> recipients,
       User initiator,
       Instant timestamp
   ) {}
   ```

3. **Add notification identification in services**
   ```java
   // In ComponentService.update()
   public Component update(Component component, User initiator) {
       Component existing = findOne(component.getUuid(), initiator);
       
       // ... update logic
       
       // Check for notification triggers
       if (!existing.getVersion().equals(component.getVersion()) && 
           component.needsUpdate()) {
           notificationService.identifyNotification(
               NotificationTrigger.UPDATE_REQUIRED,
               "Component",
               component.getUuid(),
               "Component " + component.getName() + " needs update",
               Set.of(initiator),  // or component maintainers
               initiator
           );
       }
       
       return componentOutputPort.save(component);
   }
   ```

4. **Create NotificationService (in core or infrastructure)**
   - This service would handle the actual notification logic
   - For now, just log notifications or store them for later processing

**Estimated effort**: 4-5 hours

### Phase 8: Permission Validation (Medium Priority - TODO in codebase)

**Goal**: Implement permission checking for all operations.

#### Tasks:

1. **Create PermissionService interface**
   ```java
   package com.management.cmdb.core.service;
   
   import com.management.cmdb.core.models.business.identity.User;
   
   public interface PermissionService {
       boolean canCreateProject(User user);
       boolean canUpdateProject(User user, UUID projectUuid);
       boolean canArchiveProject(User user, UUID projectUuid);
       boolean canDeleteProject(User user, UUID projectUuid);
       
       boolean canCreateEnvironment(User user, UUID projectUuid);
       boolean canUpdateEnvironment(User user, UUID environmentUuid);
       boolean canArchiveEnvironment(User user, UUID environmentUuid);
       
       boolean canCreateComponent(User user, UUID environmentUuid);
       boolean canUpdateComponent(User user, UUID componentUuid);
       // ... etc
   }
   ```

2. **Add permission checks to all service methods**
   ```java
   public Project create(Project project, User initiator) {
       if (!permissionService.canCreateProject(initiator)) {
           throw new UnauthorizedException(
               "User " + initiator.name() + " does not have permission to create projects");
       }
       // ... rest of create logic
   }
   ```

3. **Create UnauthorizedException**
   ```java
   public class UnauthorizedException extends CoreException {
       public UnauthorizedException(String message) {
           super(message);
       }
   }
   ```

**Estimated effort**: 6-8 hours

---

## Implementation Checklist by Entity

### Project
- [x] `checkIntegrity()` basic fields
- [ ] Length validation for all fields
- [ ] Uniqueness check for shortName
- [ ] Reference validation (BusinessService, UserGroups)
- [ ] Cascading archive/delete to environments
- [ ] Event creation for all operations
- [ ] Permission validation (TODO)
- [ ] Notification triggers

### BusinessService
- [x] `checkIntegrity()` basic fields
- [ ] Uniqueness check for name and abbreviation
- [ ] Abbreviation format validation (uppercase)
- [ ] Immutability enforcement for abbreviation
- [ ] Event creation for all operations
- [ ] Permission validation (TODO)

### Component
- [x] `checkIntegrity()` basic fields
- [ ] Type-specific validation (Host, VM, Hardware, Software)
- [ ] Uniqueness check within environment
- [ ] Version compatibility with Technology
- [ ] Certificate validation
- [ ] needsUpdate() integration
- [ ] Event creation for all operations
- [ ] Permission validation (TODO)

### Environment
- [ ] `checkIntegrity()` (not yet implemented)
- [ ] State transition validation
- [ ] JIRA tracker format validation
- [ ] Uniqueness check within project
- [ ] Reference validation (Project)
- [ ] Cascading archive/delete to components
- [ ] Event creation for all operations
- [ ] Notification triggers (PRODUCTION status)
- [ ] Permission validation (TODO)

### Technology
- [ ] `checkIntegrity()` (not yet implemented)
- [ ] Version relationship validation
- [ ] Uniqueness check for name
- [ ] Event creation for all operations
- [ ] Permission validation (TODO)

### UserGroup
- [x] `checkIntegrity()` basic fields
- [ ] Email format validation
- [ ] Uniqueness check for name
- [ ] Reference validation (owner, members)
- [ ] Event creation for all operations
- [ ] Permission validation (TODO)

---

## Code Organization

### New Packages to Create

```
core/src/main/java/com/management/cmdb/core/
├── models/
│   ├── exceptions/              # Existing
│   │   ├── ConflictException.java         # NEW
│   │   └── UnauthorizedException.java      # NEW
│   └── technical/              # Existing
│       └── NotificationTrigger.java      # NEW
├── service/
│   ├── PermissionService.java          # NEW (interface)
│   └── NotificationService.java        # NEW (optional)
└── utils/                       # NEW
    └── EventDescriptionBuilder.java
```

### New Methods to Add

#### Output Port Extensions

Add these methods to output ports:

```java
// ProjectOutputPort
boolean existsByShortName(String shortName);

// BusinessServiceOutputPort
boolean existsByName(String name);
boolean existsByAbbreviation(String abbreviation);

// ComponentOutputPort
boolean existsByNameAndEnvironment(String name, UUID environmentUuid);

// EnvironmentOutputPort
boolean existsByNameAndProject(String name, UUID projectUuid);

// TechnologyOutputPort
boolean existsByName(String name);

// IdentityOutputPort
boolean existsUserGroupByName(String name);
boolean existsUser(UUID uuid);
```

---

## Testing Strategy

### Unit Tests

Each business rule should have a corresponding unit test:

```java
// Example: ProjectValidationTest.java

class ProjectValidationTest {
    
    @Test
    void testProjectCreation_WithValidData_Success() {
        // Setup
        BusinessService bs = new BusinessService("HR", "HUM");
        UserGroup owners = new UserGroup("IT-Owners", "it-owners@company.com", 
                                        "IT Owners", new User(...), Set.of(new User(...)));
        
        Project project = Project.builder()
            .fullName("Human Resources System")
            .shortName("HR-SYS")
            .description("HR management system for the company")
            .businessService(bs)
            .owners(owners)
            .build();
        
        // Test
        assertDoesNotThrow(project::checkIntegrity);
    }
    
    @Test
    void testProjectCreation_WithoutFullName_ThrowsException() {
        Project project = Project.builder()
            .shortName("TEST")
            .description("Test")
            .businessService(new BusinessService("Test", "TST"))
            .owners(new UserGroup("Test", ...))
            .build();
        
        assertThrows(InvalidObjectException.class, project::checkIntegrity);
    }
    
    @Test
    void testProjectCreation_WithShortNameTooShort_ThrowsException() {
        Project project = Project.builder()
            .fullName("Test")
            .shortName("AB")  // Too short
            .description("Test")
            .businessService(new BusinessService("Test", "TST"))
            .owners(new UserGroup("Test", ...))
            .build();
        
        assertThrows(InvalidObjectException.class, project::checkIntegrity);
    }
}
```

### Integration Tests

Test complete workflows:

```java
// Example: ProjectCreationIntegrationTest.java

class ProjectCreationIntegrationTest {
    
    @Mock
    private ProjectOutputPort projectOutputPort;
    
    @Mock
    private BusinessServiceInputPort businessServiceService;
    
    @Mock
    private EnvironmentInputPort environmentService;
    
    private ProjectService projectService;
    
    @BeforeEach
    void setup() {
        projectService = new ProjectService(
            projectOutputPort, 
            businessServiceService, 
            environmentService
        );
    }
    
    @Test
    void testCreateProject_WithValidData_ReturnsProject() {
        // Setup mocks
        when(businessServiceService.findOne(anyString(), any())).thenReturn(new BusinessService("HR", "HUM"));
        when(projectOutputPort.save(any())).thenReturn(new Project());
        
        // Create request
        User initiator = new User(UUID.randomUUID(), "admin", "admin@company.com", Set.of());
        Project project = Project.builder()
            .fullName("Test Project")
            .shortName("TEST")
            .description("Test")
            .businessService(new BusinessService("HR", "HUM"))
            .owners(new UserGroup("IT", ...))
            .build();
        
        // Test
        Project result = projectService.create(project, initiator);
        
        // Verify
        assertNotNull(result);
        verify(projectOutputPort).save(any());
    }
    
    @Test
    void testCreateProject_WithDuplicateShortName_ThrowsConflictException() {
        // Setup mocks
        when(projectOutputPort.existsByShortName("TEST")).thenReturn(true);
        
        // Create request
        User initiator = User.UNKNOWN;
        Project project = Project.builder()
            .fullName("Test Project")
            .shortName("TEST")
            .description("Test")
            .businessService(new BusinessService("HR", "HUM"))
            .owners(new UserGroup("IT", ...))
            .build();
        
        // Test & Verify
        assertThrows(ConflictException.class, 
            () -> projectService.create(project, initiator));
    }
}
```

---

## Next Steps After Implementation

1. **Review all services** for consistency with documentation
2. **Add missing tests** for all validation chains
3. **Performance test** with large datasets
4. **Security review** of all permission checks
5. **Document any deviations** from this guide

---

## See Also

- [Domain Business Logic](./core-domain-logic.md) - Complete business logic overview
- [Validation Chains](./core-validation-chains.md) - Detailed validation workflows
- [Event Management](./core-event-management.md) - Event tracking and audit trails
- [Business Rules](./core-business-rules.md) - Complete rule catalog
- [Architecture Overview](../architecture.md) - System architecture
