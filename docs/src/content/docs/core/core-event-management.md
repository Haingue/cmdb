---
title: CORE Event Management
description: Comprehensive guide to event tracking, audit trails, and state change management in CMDB CORE
---

# CORE Event Management

This document describes the event management system in the CMDB CORE module, including event types, event lifecycle, audit trail implementation, and integration with domain entities.

## Overview

The CMDB CORE implements a comprehensive event tracking system that:
- Records all significant changes to domain entities
- Maintains a complete audit trail for compliance and debugging
- Supports event-driven architecture patterns
- Enables historical analysis and rollback scenarios

### Key Concepts

```
┌─────────────────────────────────────────────────────────────────┐
│                      Event Management System                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐    │
│  │     Event       │  │   EventType     │  │    Versioned    │    │
│  │                 │  │                 │  │   SavedEntity   │    │
│  │ - timestamp     │  │ - CREATE        │  │                 │    │
│  │ - type          │  │ - UPDATE        │  │ - revision      │    │
│  │ - description   │  │ - ARCHIVE       │  │ - events        │    │
│  │ - initiator     │  │ - DELETE        │  │ - creationDate  │    │
│  └─────────────────┘  └─────────────────┘  │ - archiveDate   │    │
│                                            └─────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                    Event Flow                                 │  │
│  │                                                                  │  │
│  │  User/System Action ────▶ Entity State Change ────▶ Event     │  │
│  │                          │                        │ Creation      │  │
│  │                          │                        │ + Add to      │  │
│  │                          ▼                        │   entity.events│  │
│  │                     Entity Method              │   + Increment  │  │
│  │                     (e.g., setFullName)         │   revision     │  │
│  │                          │                        ▼               │  │
│  │                          ▼                                   │  │
│  │                     Persist Entity with Events                │  │
│  │                                                                  │  │
│  └─────────────────────────────────────────────────────────────┘  │
│                                                                      │
└─────────────────────────────────────────────────────────────────┘
```

## Event Model

### Event Class

```java
package com.management.cmdb.core.models.technical;

import java.time.Instant;
import java.util.Objects;

public class Event {
    private Instant timestamp;
    private EventType type;
    private String description;
    private String initiator;
    
    // Constructors
    public Event(EventType type, String description, String initiator) {
        this.initiator = initiator;
        this.timestamp = Instant.now();
        this.type = type;
        this.description = description;
    }
    
    public Event(Instant timestamp, EventType type, 
                String description, String initiator) {
        this.timestamp = timestamp;
        this.type = type;
        this.description = description;
        this.initiator = initiator;
    }
    
    // Getters and Setters
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description; 
    }
    public String getInitiator() { return initiator; }
    public void setInitiator(String initiator) { this.initiator = initiator; }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event event)) return false;
        return Objects.equals(timestamp, event.timestamp) 
            && type == event.type 
            && Objects.equals(initiator, event.initiator);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(timestamp, type, initiator);
    }
}
```

### EventType Enum

```java
package com.management.cmdb.core.models.technical;

public enum EventType {
    CREATE,    // Entity was created
    UPDATE,    // Entity was modified
    ARCHIVE,   // Entity was archived (soft delete)
    DELETE     // Entity was permanently deleted
}
```

**Event Type Descriptions**:

| Type | When Used | Example |
|------|-----------|---------|
| `CREATE` | First time an entity is persisted | "Project created: HR-SYS" |
| `UPDATE` | Any modification to existing entity | "Project updated: changed fullName" |
| `ARCHIVE` | Entity is soft-deleted (marked as archived) | "Project archived by admin" |
| `DELETE` | Entity is permanently removed (rare) | "Project permanently deleted" |

## Event Lifecycle

### Creation Process

```
1. User/System initiates action (create, update, archive, delete)
   │
2. Service layer validates request
   │
3. Service modifies entity state
   │
4. Service creates Event object:
   - type: corresponding EventType
   - initiator: user UUID or system name
   - description: human-readable change description
   - timestamp: automatically set to Instant.now()
   │
5. Event is added to entity.events list
   │
6. Entity revision is incremented
   │
7. Entity is persisted with new event
   │
8. Event is available for audit queries
```

### Event Creation Patterns

#### Pattern 1: Direct Event Creation

```java
// In service layer
Project project = Project.builder()
    .fullName("New Project")
    .shortName("NEW")
    .build();

// Create and add CREATE event
Event createEvent = new Event(
    EventType.CREATE,
    "Project created: NEW",
    initiator.uuid().toString()
);
project.getEvents().add(createEvent);
project.setRevision(1);
```

#### Pattern 2: Using VersionedSavedEntity Methods

```java
// In service layer - using built-in methods
Project project = new Project(...);

// This automatically:
// 1. Increments revision
// 2. Creates event with UPDATE type
// 3. Adds to events list
project.update(initiator.uuid().toString());

// Or with User object
project.update(initiator);

// Or with custom Event
Event customEvent = new Event(
    EventType.UPDATE,
    "Custom update description",
    initiator.uuid().toString()
);
project.update(customEvent);
```

#### Pattern 3: Archive with Automatic Event

```java
// In service layer
Project project = projectService.findOne(uuid, initiator);

// This automatically:
// 1. Sets archiveDatetime
// 2. Creates ARCHIVE event
// 3. Increments revision
project.archive(initiator.uuid().toString());

// Or with specific timestamp
project.archive(initiator.uuid().toString(), LocalDateTime.now());
```

## Audit Trail Implementation

### VersionedSavedEntity Base Class

```java
package com.management.cmdb.core.models.technical;

import com.management.cmdb.core.models.business.identity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VersionedSavedEntity extends UniqueEntity implements Serializable {
    
    protected long revision;
    protected List<Event> events;
    protected LocalDateTime creationDatetime;
    protected LocalDateTime archiveDatetime;
    
    // Version management
    protected void increaseVersion() {
        this.revision++;
    }
    
    // Update methods
    public boolean update(User user) {
        return this.update(user.uuid().toString());
    }
    
    public boolean update(String initiator) {
        return this.update(new Event(EventType.UPDATE, null, initiator));
    }
    
    public boolean update(Event event) {
        this.increaseVersion();
        return this.events.add(event);
    }
    
    // Archive methods
    public boolean isArchived() {
        return archiveDatetime != null;
    }
    
    public void archive(User user) {
        this.archive(user.uuid().toString(), LocalDateTime.now());
    }
    
    public void archive(String initiator) {
        this.archive(initiator, LocalDateTime.now());
    }
    
    public void archive(String initiator, LocalDateTime archiveDatetime) {
        this.archiveDatetime = archiveDatetime;
        this.update(new Event(EventType.ARCHIVE, null, initiator));
    }
}
```

### Audit Trail Components

Every VersionedSavedEntity maintains:

```
┌─────────────────────────────────────────────────────────────┐
│                    Audit Trail Components                        │
├─────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. revision: long                                               │
│     - Monotonically increasing counter                        │
│     - Starts at 0 (or 1 for first create)                      │
│     - Incremented on every state change                         │
│                                                                 │
│  2. events: List<Event>                                         │
│     - Complete history of all changes                          │
│     - Ordered by timestamp (oldest first)                      │
│     - Immutable once created (new events appended)            │
│                                                                 │
│  3. creationDatetime: LocalDateTime                            │
│     - When entity was first created                            │
│     - Set once, never changed                                   │
│                                                                 │
│  4. archiveDatetime: LocalDateTime                              │
│     - When entity was archived (soft deleted)                   │
│     - Null if entity is active                                  │
│     - Set once, never changed                                   │
│                                                                 │
│  5. uuid: UUID (from UniqueEntity)                              │
│     - Unique identifier                                         │
│     - Set once, never changed                                   │
│                                                                 │
└─────────────────────────────────────────────────────────────┘
```

## Event Integration by Entity

### Project Event Integration

**Events Generated**:
- `CREATE`: When project is first created
- `UPDATE`: When any field is modified
- `ARCHIVE`: When project is archived

**Event Creation in ProjectService**:

```java
// In ProjectService.create()
@Override
public Project create(Project newEntity, User initiator) {
    Project project = Project.builder()
        .fullName(newEntity.getFullName())
        .shortName(newEntity.getShortName())
        .description(newEntity.getDescription())
        .businessService(newEntity.getBusinessService())
        .maintainers(newEntity.getMaintainers())
        .owners(newEntity.getOwners())
        .build();
    
    project.checkIntegrity();
    
    // Add CREATE event
    project.update(new Event(
        EventType.CREATE,
        "Project created: " + project.getShortName(),
        initiator.uuid().toString()
    ));
    
    // ... save to repository
    return this.projectOutputPort.save(project);
}

// In ProjectService.update()
@Override
public Project update(Project project, User initiator) {
    // ... validation and field updates
    
    // Add UPDATE event with change description
    String changeDescription = buildChangeDescription(existingProject, project);
    existingProject.update(new Event(
        EventType.UPDATE,
        changeDescription,
        initiator.uuid().toString()
    ));
    
    return this.projectOutputPort.save(existingProject);
}

// In ProjectService.archive()
@Override
public void archive(UUID uuid, User initiator) {
    Project project = this.findOne(uuid, initiator);
    
    // Archive all environments first
    project.getEnvironments().forEach(environment -> 
        this.environmentService.archive(environment.getUuid(), initiator)
    );
    
    // Archive the project
    project.archive(initiator.uuid().toString());
    this.projectOutputPort.save(project);
}
```

### Component Event Integration

**Events Generated**:
- `CREATE`: When component is created
- `UPDATE`: When component fields are modified
- `ARCHIVE`: When component is archived

**Special Considerations**:
- Version changes should include version info in description
- Technology changes should note both old and new technology
- Certificate updates should be highlighted

**Example**:

```java
// In ComponentService.update()
public Component update(Component component, User initiator) {
    Component existing = findOne(component.getUuid(), initiator);
    
    // Build change description
    StringBuilder changes = new StringBuilder();
    if (!Objects.equals(existing.getVersion(), component.getVersion())) {
        changes.append("version: ").append(existing.getVersion()) 
               .append(" -> ").append(component.getVersion());
    }
    if (!Objects.equals(existing.getTechnology(), component.getTechnology())) {
        if (changes.length() > 0) changes.append(", ");
        changes.append("technology: ").append(existing.getTechnology().getName())
               .append(" -> ").append(component.getTechnology().getName());
    }
    
    // Update fields
    existing.updateFrom(component);
    
    // Add event
    existing.update(new Event(
        EventType.UPDATE,
        "Component updated: " + changes.toString(),
        initiator.uuid().toString()
    ));
    
    return componentOutputPort.save(existing);
}
```

### Environment Event Integration

**Events Generated**:
- `CREATE`: When environment is created
- `UPDATE`: When environment fields are modified
- `ARCHIVE`: When environment is archived

**Special Considerations**:
- Status changes should trigger notifications
- PRODUCTION status changes are especially important

**Example**:

```java
// In EnvironmentService.update()
public Environment update(Environment environment, User initiator) {
    Environment existing = findOne(environment.getUuid(), initiator);
    
    // Check for status change
    EnvironmentStatus oldStatus = existing.getStatus();
    EnvironmentStatus newStatus = environment.getStatus();
    
    // Update fields
    existing.setName(environment.getName());
    existing.setLocation(environment.getLocation());
    existing.setType(environment.getType());
    existing.setStatus(newStatus);
    
    // Build description
    String description = "Environment updated";
    if (oldStatus != newStatus) {
        description += ": status changed from " + oldStatus + " to " + newStatus;
        
        // Trigger notification for PRODUCTION status
        if (newStatus == EnvironmentStatus.PRODUCTION) {
            notifyProductionDeployment(existing, initiator);
        }
    }
    
    // Add event
    existing.update(new Event(
        EventType.UPDATE,
        description,
        initiator.uuid().toString()
    ));
    
    return environmentOutputPort.save(existing);
}
```

### BusinessService Event Integration

**Events Generated**:
- `CREATE`: When business service is created
- `UPDATE`: When business service fields are modified (except abbreviation)
- `ARCHIVE`: When business service is archived

**Special Considerations**:
- Abbreviation cannot be changed (immutable)
- Changing name affects all associated projects

### Technology Event Integration

**Events Generated**:
- `CREATE`: When technology is created
- `UPDATE`: When technology fields are modified
- `ARCHIVE`: When technology is archived

**Special Considerations**:
- Changing minimalVersion affects needsUpdate() for all components
- Version field changes should be carefully validated

## Audit Query Patterns

### Basic Queries

```java
// Get all events for an entity
List<Event> allEvents = entity.getEvents();

// Get events of specific type
List<Event> createEvents = entity.getEvents().stream()
    .filter(e -> e.getType() == EventType.CREATE)
    .collect(Collectors.toList());

// Get all UPDATE events
List<Event> updateEvents = entity.getEvents().stream()
    .filter(e -> e.getType() == EventType.UPDATE)
    .collect(Collectors.toList());

// Check if entity has been archived
boolean isArchived = entity.isArchived();
// Or: 
boolean hasArchiveEvent = entity.getEvents().stream()
    .anyMatch(e -> e.getType() == EventType.ARCHIVE);
```

### Historical Analysis

```java
// Get first creation event
Optional<Event> creationEvent = entity.getEvents().stream()
    .filter(e -> e.getType() == EventType.CREATE)
    .findFirst();

// Get last update event
Optional<Event> lastUpdate = entity.getEvents().stream()
    .filter(e -> e.getType() == EventType.UPDATE)
    .reduce((first, second) -> second);

// Get all initiators who modified this entity
Set<String> initiators = entity.getEvents().stream()
    .map(Event::getInitiator)
    .collect(Collectors.toSet());

// Get timeline of changes
Map<EventType, Long> eventCounts = entity.getEvents().stream()
    .collect(Collectors.groupingBy(Event::getType, Collectors.counting()));
```

### Advanced Queries

```java
// Get all changes made by a specific user
List<Event> userChanges = entity.getEvents().stream()
    .filter(e -> e.getInitiator().equals(userUuid.toString()))
    .collect(Collectors.toList());

// Get all changes in a time range
Instant start = Instant.parse("2024-01-01T00:00:00Z");
Instant end = Instant.parse("2024-01-31T23:59:59Z");
List<Event> januaryChanges = entity.getEvents().stream()
    .filter(e -> !e.getTimestamp().isBefore(start))
    .filter(e -> !e.getTimestamp().isAfter(end))
    .collect(Collectors.toList());

// Get most recent change
Optional<Event> mostRecent = entity.getEvents().stream()
    .max(Comparator.comparing(Event::getTimestamp));

// Get change frequency
long updateCount = entity.getEvents().stream()
    .filter(e -> e.getType() == EventType.UPDATE)
    .count();
```

### Cross-Entity Audit

```java
// Get all events for all projects
List<Event> allProjectEvents = projectOutputPort.findAll().stream()
    .flatMap(p -> p.getEvents().stream())
    .collect(Collectors.toList());

// Get all CREATE events for a business service's projects
List<Event> bsCreateEvents = businessService.getProjects().stream()
    .flatMap(p -> p.getEvents().stream())
    .filter(e -> e.getType() == EventType.CREATE)
    .collect(Collectors.toList());

// Find entities modified by a specific user
List<VersionedSavedEntity> userModifiedEntities = 
    allEntities.stream()
        .filter(e -> e.getEvents().stream()
            .anyMatch(event -> event.getInitiator().equals(userUuid)))
        .collect(Collectors.toList());
```

## Event Description Patterns

### Recommended Description Formats

**Create Events**:
```
"EntityType created: identifier"
"Project created: HR-SYS"
"Component created: web-server-01"
"Environment created: PROD"
```

**Update Events**:
```
"EntityType updated: field1=old->new, field2=old->new"
"Project updated: fullName=Old Name->New Name"
"Component updated: version=1.0.0->1.1.0, technology=Java 11->Java 17"
"Environment updated: status=TEST->PRODUCTION"
```

**Archive Events**:
```
"EntityType archived"
"Project archived by admin"
"Component archived: end of life"
```

**Delete Events**:
```
"EntityType permanently deleted"
"Project deleted: cleanup"
```

### Description Building Helper

```java
public class EventDescriptionBuilder {
    private final StringBuilder sb = new StringBuilder();
    private boolean first = true;
    
    public EventDescriptionBuilder(String entityType, String identifier) {
        sb.append(entityType).append(" ").append(identifier.isEmpty() ? "" : identifier + " ");
    }
    
    public EventDescriptionBuilder created() {
        sb.append("created");
        return this;
    }
    
    public EventDescriptionBuilder updated() {
        sb.append("updated");
        return this;
    }
    
    public EventDescriptionBuilder archived() {
        sb.append("archived");
        return this;
    }
    
    public EventDescriptionBuilder deleted() {
        sb.append("permanently deleted");
        return this;
    }
    
    public EventDescriptionBuilder changed(String field, Object oldValue, Object newValue) {
        if (first) {
            sb.append(": ");
            first = false;
        } else {
            sb.append(", ");
        }
        sb.append(field).append("=").append(oldValue).append("->").append(newValue);
        return this;
    }
    
    public String build() {
        return sb.toString();
    }
}

// Usage:
EventDescriptionBuilder builder = new EventDescriptionBuilder("Project", "HR-SYS");
String description = builder
    .created()
    .build();
// Result: "Project HR-SYS created"

// For update:
String updateDesc = new EventDescriptionBuilder("Project", "HR-SYS")
    .updated()
    .changed("fullName", "Old Name", "New Name")
    .changed("description", oldDesc, newDesc)
    .build();
// Result: "Project HR-SYS updated: fullName=Old Name->New Name, description=..."
```

## Event-Driven Architecture Patterns

### Event Listeners (Future Enhancement)

While not currently implemented, the event system can be extended to support event listeners:

```java
// Interface for event listeners
public interface DomainEventListener<T extends VersionedSavedEntity> {
    void onEvent(T entity, Event event);
}

// Registration system
public class EventDispatcher {
    private final Map<Class<?>, List<DomainEventListener<?>>> listeners = new HashMap<>();
    
    public <T extends VersionedSavedEntity> void registerListener(
            Class<T> entityClass, 
            DomainEventListener<T> listener) {
        listeners.computeIfAbsent(entityClass, k -> new ArrayList<>())
                .add(listener);
    }
    
    public <T extends VersionedSavedEntity> void dispatch(T entity, Event event) {
        List<DomainEventListener<?>> entityListeners = listeners.get(entity.getClass());
        if (entityListeners != null) {
            entityListeners.forEach(listener -> 
                ((DomainEventListener<T>) listener).onEvent(entity, event));
        }
    }
}

// Usage in service:
public class NotificationListener implements DomainEventListener<Project> {
    @Override
    public void onEvent(Project project, Event event) {
        if (event.getType() == EventType.CREATE) {
            notifyProjectCreated(project, event.getInitiator());
        }
    }
}

// Register listener
eventDispatcher.registerListener(Project.class, new NotificationListener());

// In ProjectService.create():
project.update(event);
eventDispatcher.dispatch(project, event);
```

### Event Sourcing (Future Consideration)

For full auditability, consider implementing event sourcing pattern:

```java
// Instead of storing current state, store all events
// and reconstruct state by replaying events
public class EventSourcedEntity extends UniqueEntity {
    private final List<Event> events = new ArrayList<>();
    
    public void applyEvent(Event event) {
        events.add(event);
        // Apply event to current state
        handleEvent(event);
    }
    
    public void handleEvent(Event event) {
        switch (event.getType()) {
            case CREATE:
                handleCreate(event);
                break;
            case UPDATE:
                handleUpdate(event);
                break;
            case ARCHIVE:
                handleArchive(event);
                break;
            // ...
        }
    }
    
    // Rebuild state from events
    public static EventSourcedEntity fromEvents(List<Event> events) {
        EventSourcedEntity entity = new EventSourcedEntity();
        events.forEach(entity::handleEvent);
        return entity;
    }
}
```

## Event Retention Policy

### Current Implementation

- All events are retained indefinitely
- Events are stored in-memory (as part of entity)
- Events are persisted to database with the entity

### Recommended Future Enhancements

1. **Event Pruning**: Remove old events after certain period
   ```java
   // Keep only last N events
   public void pruneEvents(int maxEvents) {
       if (this.events.size() > maxEvents) {
           this.events = this.events.subList(
               this.events.size() - maxEvents, 
               this.events.size()
           );
       }
   }
   ```

2. **Event Archiving**: Move old events to separate archive table
3. **Event Compression**: Summarize multiple updates into single event

## Integration with External Systems

### Audit Log Export

```java
// Export audit trail to CSV
public String exportAuditLogToCsv(VersionedSavedEntity entity) {
    StringBuilder csv = new StringBuilder();
    csv.append("timestamp,type,description,initiator,revision\n");
    
    for (Event event : entity.getEvents()) {
        csv.append(String.format("%s,%s,"%s","%s",%d\n",
            event.getTimestamp(),
            event.getType(),
            event.getDescription().replace("\"", "\"\""),
            event.getInitiator(),
            entity.getRevision()));
    }
    
    return csv.toString();
}
```

### SIEM Integration

```java
// Format events for SIEM systems
public List<Map<String, Object>> toSiemEvents(VersionedSavedEntity entity) {
    return entity.getEvents().stream()
        .map(event -> {
            Map<String, Object> siemEvent = new HashMap<>();
            siemEvent.put("timestamp", event.getTimestamp().toString());
            siemEvent.put("eventType", "CMDB_" + event.getType());
            siemEvent.put("entityType", entity.getClass().getSimpleName());
            siemEvent.put("entityId", entity.getUuid().toString());
            siemEvent.put("initiator", event.getInitiator());
            siemEvent.put("description", event.getDescription());
            siemEvent.put("revision", entity.getRevision());
            return siemEvent;
        })
        .collect(Collectors.toList());
}
```

## Testing Event Management

### Unit Tests for Event Creation

```java
@Test
void testCreateProject_CreatesCreateEvent() {
    // Given
    Project project = Project.builder()
        .fullName("Test Project")
        .shortName("TEST")
        .description("Test")
        .businessService(new BusinessService("Test BS", "TST"))
        .owners(new UserGroup("Owners", ...))
        .build();
    
    User initiator = User.UNKNOWN;
    
    // When
    project.update(new Event(EventType.CREATE, "Project created", initiator.uuid().toString()));
    
    // Then
    assertEquals(1, project.getRevision());
    assertEquals(1, project.getEvents().size());
    assertEquals(EventType.CREATE, project.getEvents().get(0).getType());
    assertEquals(initiator.uuid().toString(), project.getEvents().get(0).getInitiator());
}

@Test
void testArchiveProject_CreatesArchiveEvent() {
    // Given
    Project project = new Project(...);
    project.setRevision(5);
    User initiator = new User(UUID.randomUUID(), "admin", ...);
    
    // When
    project.archive(initiator);
    
    // Then
    assertEquals(6, project.getRevision());
    assertTrue(project.isArchived());
    assertEquals(1, project.getEvents().stream()
        .filter(e -> e.getType() == EventType.ARCHIVE)
        .count());
}

@Test
void testMultipleUpdates_IncrementRevision() {
    // Given
    Project project = new Project(...);
    User initiator = User.UNKNOWN;
    
    // When
    project.update(initiator);
    project.update(initiator);
    project.update(initiator);
    
    // Then
    assertEquals(3, project.getRevision());
    assertEquals(3, project.getEvents().size());
    assertTrue(project.getEvents().stream()
        .allMatch(e -> e.getType() == EventType.UPDATE));
}
```

### Integration Tests for Event Persistence

```java
@Test
void testProjectCreation_PersistsEvents() {
    // Given
    ProjectCreationRequest request = new ProjectCreationRequest(...);
    
    // When
    Project created = projectService.create(request);
    
    // Reload from database
    Project loaded = projectService.findOne(created.getUuid(), User.UNKNOWN);
    
    // Then
    assertNotNull(loaded.getEvents());
    assertFalse(loaded.getEvents().isEmpty());
    assertEquals(1, loaded.getEvents().stream()
        .filter(e -> e.getType() == EventType.CREATE)
        .count());
}
```

## Performance Considerations

### Event List Size

- Events list grows with each modification
- Consider limiting maximum number of events per entity
- Old events can be pruned or archived

### Query Performance

- Querying events directly is O(1) for in-memory entities
- Database queries on events may need optimization
- Consider adding indexes on event fields

### Memory Usage

- Each Event object is relatively small (~100-200 bytes)
- 1000 events per entity ≈ 100-200 KB
- Monitor memory usage for entities with many modifications

## Best Practices

### DO:
1. ✅ Always create events for state changes
2. ✅ Use meaningful event descriptions
3. ✅ Include initiator in every event
4. ✅ Use appropriate EventType
5. ✅ Increment revision for every change
6. ✅ Handle events atomically with state changes

### DON'T:
1. ❌ Create events without corresponding state change
2. ❌ Forget to increment revision
3. ❌ Use null or empty initiator
4. ❌ Create events for read-only operations
5. ❌ Modify existing events (they should be immutable)

### Event Creation Checklist

- [ ] State change has occurred
- [ ] Event type is correct (CREATE, UPDATE, ARCHIVE, DELETE)
- [ ] Initiator is set (user UUID or system name)
- [ ] Description is meaningful
- [ ] Event is added to entity.events list
- [ ] Revision is incremented
- [ ] Entity is persisted

## See Also

- [Domain Business Logic](./core-domain-logic.md)
- [Validation Chains](./core-validation-chains.md)
- [Business Rules](./core-business-rules.md)
- [Architecture Overview](../architecture.md)
