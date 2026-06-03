# ADR-005: Use Kafka for Event-Driven Architecture

## Status

Accepted

## Context

The CMDB project currently uses synchronous REST/HTTP communication between services. As the project grows and more services are added, we need a solution for:

- **Asynchronous processing**: Long-running operations that shouldn't block the caller
- **Event propagation**: Notifying multiple services about changes without tight coupling
- **Decoupling**: Reducing direct dependencies between services
- **Scalability**: Handling spikes in load without overwhelming services
- **Reliability**: Ensuring events are not lost even if a service is temporarily unavailable

Current pain points:
- BFF must wait for all service responses before returning to the frontend
- No way to notify external systems about changes in the CMDB
- Aggregators push data directly to Inventory Service, creating tight coupling
- Difficult to add new consumers of existing data without modifying producers

## Decision

We adopt **Apache Kafka** as our event streaming platform for event-driven communication between services.

### Kafka Topics Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                         Kafka Cluster                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────────────┐  ┌──────────────────────┐              │
│  │  cmdb.entity.events   │  │  cmdb.project.events  │              │
│  │  (Entity CRUD events) │  │  (Project CRUD events)│              │
│  └──────────┬───────────┘  └──────────┬───────────┘              │
│              │                         │                          │
│  ┌───────────▼───────────┐  ┌─────────▼──────────┐              │
│  │  cmdb.aggregator.github│  │cmdb.aggregator.syslog│              │
│  │  (GitHub sync events)  │  │  (Syslog events)      │              │
│  └───────────────────────┘  └─────────────────────┘              │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                    Dead Letter Queue (DLQ)                    │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Event Types

#### Entity Events
- `EntityCreatedEvent` - Fired when a new entity is created
- `EntityUpdatedEvent` - Fired when an entity is updated
- `EntityDeletedEvent` - Fired when an entity is deleted
- `EntityLinkedEvent` - Fired when entities are linked
- `EntityUnlinkedEvent` - Fired when entities are unlinked

#### Project Events
- `ProjectCreatedEvent` - Fired when a new project is created
- `ProjectUpdatedEvent` - Fired when a project is updated
- `ProjectDeletedEvent` - Fired when a project is deleted

#### Aggregator Events
- `GitHubRepoSyncedEvent` - Fired when a GitHub repository is synced
- `SyslogMessageProcessedEvent` - Fired when a syslog message is processed

### Implementation Pattern

Each service that produces or consumes events implements:

```java
// Event Producer Interface (in core domain)
public interface EventPublisher<T> {
    void publish(T event);
    CompletableFuture<Void> publishAsync(T event);
}

// Kafka Implementation (in infrastructure)
public class KafkaEventPublisher<T> implements EventPublisher<T> {
    private final KafkaTemplate<String, T> kafkaTemplate;
    private final String topic;
    
    @Override
    public void publish(T event) {
        kafkaTemplate.send(topic, event.getAggregateId().toString(), event);
    }
    
    @Override
    public CompletableFuture<Void> publishAsync(T event) {
        return kafkaTemplate.send(topic, event.getAggregateId().toString(), event)
                .toCompletableFuture()
                .thenApply(result -> null);
    }
}

// Event Consumer
@Component
public class EntityEventConsumer {
    @KafkaListener(topics = "cmdb.entity.events", groupId = "inventory-service")
    public void handleEntityEvent(EntityEvent event) {
        // Process event
    }
}
```

### Event Format

All events follow a standard envelope format:

```json
{
  "header": {
    "eventId": "550e8400-e29b-41d4-a716-446655440000",
    "eventType": "EntityCreatedEvent",
    "timestamp": "2025-06-03T10:00:00Z",
    "version": "1.0",
    "source": "inventory-service",
    "correlationId": "request-12345"
  },
  "payload": {
    "entityId": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Production Server",
    "type": "HARDWARE",
    "createdAt": "2025-06-03T10:00:00Z",
    "createdBy": "user@example.com"
  }
}
```

## Consequences

### Positive

- **Decoupling**: Services don't need to know about each other; they only need to know about events
- **Scalability**: Consumers can process events at their own pace, handling load spikes
- **Resilience**: If a consumer fails, events remain in Kafka for retry
- **Flexibility**: Easy to add new event consumers without modifying producers
- **Asynchronous**: Non-critical operations can be handled asynchronously
- **Audit Trail**: All events are persisted, creating a complete audit log
- **Replayability**: Can replay events to rebuild state or for debugging

### Negative

- **Complexity**: Adds Kafka infrastructure to manage
- **Eventual Consistency**: Systems may be temporarily out of sync
- **Ordering**: Need to handle out-of-order event delivery
- **Duplicate Events**: Must handle potential duplicate event processing
- **Monitoring**: Need to monitor Kafka cluster health, lag, etc.
- **Learning Curve**: Team needs to learn Kafka concepts and best practices

## Alternatives Considered

### 1. RabbitMQ
- **Considered**: Simpler to set up and manage
- **Considered**: Good for simple messaging patterns
- **Rejected**: Less scalable for high-throughput scenarios
- **Rejected**: No built-in event streaming capabilities
- **Rejected**: No persistent log of all events

### 2. AWS SNS/SQS
- **Considered**: Managed service, no infrastructure to maintain
- **Considered**: Good integration with AWS ecosystem
- **Rejected**: Vendor lock-in to AWS
- **Rejected**: Limited event streaming capabilities
- **Rejected**: More expensive at scale

### 3. NATS / JetStream
- **Considered**: Lightweight, high-performance messaging
- **Considered**: Simpler than Kafka
- **Rejected**: Less mature event streaming capabilities
- **Rejected**: Smaller community and ecosystem

### 4. Redis Streams
- **Considered**: Already use Redis, could leverage existing infrastructure
- **Considered**: Simple to set up
- **Rejected**: Not designed for high-throughput event streaming
- **Rejected**: Limited durability guarantees
- **Rejected**: No built-in consumer group management

### 5. Pulsar
- **Considered**: Multi-tenancy, tiered storage
- **Considered**: Geared for cloud-native applications
- **Rejected**: More complex than Kafka
- **Rejected**: Smaller community than Kafka

## Migration Plan

### Phase 1: Infrastructure Setup
1. Deploy Kafka cluster (single node initially, then scale)
2. Configure monitoring and alerting for Kafka
3. Set up topic naming conventions and retention policies

### Phase 2: Core Event Infrastructure
1. Create event base classes and interfaces in `core/`
2. Implement Kafka producer configuration
3. Implement Kafka consumer configuration
4. Add event serialization/deserialization (JSON, Avro)

### Phase 3: Event Producers
1. Modify Inventory Service to emit entity events
2. Modify Aggregators to emit sync events
3. Add event emission to BFF for user actions

### Phase 4: Event Consumers
1. Create consumers for entity events
2. Create consumers for project events
3. Create consumers for aggregator events

### Phase 5: Enhancements
1. Add DLQ (Dead Letter Queue) for failed events
2. Implement event replay capabilities
3. Add event schema registry
4. Implement event versioning strategy

## Success Metrics

- All services can produce and consume events
- No direct REST calls between services (except for synchronous queries)
- Event processing lag is consistently low (< 1 second)
- No event loss during service outages
- Ability to replay past events when needed
