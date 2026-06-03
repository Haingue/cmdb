# Kafka Infrastructure with KRaft (No Zookeeper)

This directory contains a simplified Docker Compose configuration for running **Apache Kafka 4.2.1** with **KRaft** (Kafka Raft) instead of Zookeeper.

## Why KRaft?

### ✅ Advantages of KRaft over Zookeeper

| Feature | KRaft | Zookeeper |
|---------|-------|-----------|
| **External Dependency** | ❌ None | ✅ Required |
| **Simplicity** | ✅ Single service | ❌ Two services (Kafka + Zookeeper) |
| **Performance** | ✅ Better (direct metadata access) | ⚠️ Good |
| **Scalability** | ✅ Better | ⚠️ Limited by Zookeeper |
| **Fault Tolerance** | ✅ Better recovery | ✅ Good |
| **Multi-DC Support** | ✅ Native support | ❌ Complex |
| **Maturity** | ✅ Stable (since Kafka 3.3) | ✅ Very mature |

### 🎯 For CMDB Project
- **✅ Perfect for development**: No need for external Zookeeper
- **✅ Simpler deployment**: Only Kafka containers
- **✅ Better integration**: Metadata stored directly in Kafka
- **✅ Future-proof**: KRaft is the future of Kafka

---

## Quick Start

### 1. Start the Kafka Cluster

```bash
# From the infrastructure/kafka directory
cd infrastructure/kafka
docker-compose up -d
```

This will start:
- **3 Kafka Brokers** (KRaft mode) on ports 9092, 9093, 9094
- **Kafka UI** on port 8080
- **Topic Initialization** service that creates all CMDB topics automatically

### 2. Access Kafka UI

Open your browser and go to: [http://localhost:8080](http://localhost:8080)

### 3. Verify Cluster Health

```bash
# Check running containers
docker-compose ps

# Check all topics
docker exec -it cmdb-kafka-1 kafka-topics --bootstrap-server localhost:9092 --list
```

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Docker Network: cmdb-kafka-net               │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │ Kafka Broker  │    │ Kafka Broker  │    │ Kafka Broker  │     │
│  │    Node 1     │    │    Node 2     │    │    Node 3     │     │
│  │ (9092/29092) │    │ (9093/29092) │    │ (9094/29092) │     │
│  │ KRaft Mode   │    │ KRaft Mode   │    │ KRaft Mode   │     │
│  └──────┬───────┘    └──────┬───────┘    └──────┬───────┘     │
│         │                  │                  │                │
│         └──────────┬───────┴──────────┬───────┘                │
│                    │     KRaft Quorum    │                         │
│                    ▼                  ▼                         │
│         ┌──────────────────────────────────────────────────┐   │
│         │              Shared Metadata & Log                 │   │
│         └──────────────────────────────────────────────────┘   │
│                                    │                             │
│         ┌──────────────────────────▼──────────────────────────┐ │
│         │                  Kafka Init                          │ │
│         │              (Creates Topics)                         │ │
│         └─────────────────────────────────────────────────────┘ │
│                                    │                             │
│         ┌──────────────────────────▼──────────────────────────┐ │
│         │                  Kafka UI (Port 8080)                  │ │
│         └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                          │
                          ▼
                [Host Machine]
        Brokers: localhost:9092, localhost:9093, localhost:9094
              UI: http://localhost:8080
```

---

## Service Ports

| Service | Container Name | External Port | Internal Port | Purpose |
|---------|----------------|---------------|---------------|---------|
| Kafka Broker 1 | cmdb-kafka-1 | 9092 | 9092 | Broker API |
| Kafka Broker 1 | cmdb-kafka-1 | - | 29092 | Controller API (KRaft) |
| Kafka Broker 2 | cmdb-kafka-2 | 9093 | 9092 | Broker API |
| Kafka Broker 2 | cmdb-kafka-2 | - | 29092 | Controller API (KRaft) |
| Kafka Broker 3 | cmdb-kafka-3 | 9094 | 9092 | Broker API |
| Kafka Broker 3 | cmdb-kafka-3 | - | 29092 | Controller API (KRaft) |
| Kafka UI | cmdb-kafka-ui | 8080 | 8080 | Web Management Interface |
| Kafka Init | cmdb-kafka-init | - | - | Creates topics on startup |

---

## Pre-Configured Topics

The `kafka-init` service automatically creates the following topics when the cluster starts:

| Topic Name | Partitions | Replication Factor | Retention | Purpose |
|------------|-----------|-------------------|----------|---------|
| `cmdb.entity.events` | 3 | 2 | 7 days | Entity CRUD events |
| `cmdb.project.events` | 3 | 2 | 7 days | Project CRUD events |
| `cmdb.environment.events` | 3 | 2 | 7 days | Environment events |
| `cmdb.component.events` | 3 | 2 | 7 days | Component events |
| `cmdb.aggregator.github` | 3 | 2 | 7 days | GitHub aggregator events |
| `cmdb.aggregator.syslog` | 3 | 2 | 7 days | Syslog aggregator events |
| `cmdb.dlq` | 3 | 2 | 30 days | Dead Letter Queue |

---

## Custom Topics via Environment Variables

You can create **additional custom topics** at startup using the `KAFKA_CREATE_TOPICS` environment variable.

### Usage

Set the `KAFKA_CREATE_TOPICS` variable in your `.env` file or directly in `docker-compose.yml`:

#### Format:
```
KAFKA_CREATE_TOPICS="topic1:partitions:replication[:retention_ms],topic2:partitions:replication[:retention_ms]"
```

#### Parameters:
| Position | Parameter | Type | Default | Description |
|----------|-----------|------|---------|-------------|
| 1 | topic_name | string | - | Name of the topic |
| 2 | partitions | integer | 3 | Number of partitions |
| 3 | replication | integer | 2 | Replication factor |
| 4 | retention_ms | integer | 604800000 (7 days) | Retention in milliseconds |

#### Examples:

```bash
# Create topics with default retention (7 days)
KAFKA_CREATE_TOPICS="my-topic:3:2,my-queue:1:2"

# Create topics with custom retention
KAFKA_CREATE_TOPICS="audit-logs:3:2:2592000000,temp-data:3:2:86400000"
# audit-logs: 3 partitions, 2 replicas, 30 days retention
# temp-data: 3 partitions, 2 replicas, 1 day retention

# Create a single topic
KAFKA_CREATE_TOPICS="notifications:3:2"
```

#### Using .env file:

Create a `.env` file in the `infrastructure/kafka/` directory:

```bash
# .env
KAFKA_CREATE_TOPICS="my-custom-topic:3:2:604800000,another-topic:1:2"
KAFKA_SKIP_DEFAULT_TOPICS="false"
```

Then start the cluster:

```bash
docker-compose --env-file .env up -d
```

#### Using docker-compose override:

Create a `docker-compose.override.yml`:

```yaml
version: '3.8'
services:
  kafka-init:
    environment:
      KAFKA_CREATE_TOPICS: "custom-topic:3:2,my-queue:1:2"
```

#### Via command line:

```bash
KAFKA_CREATE_TOPICS="my-topic:3:2" docker-compose up -d
```

### Skipping Default Topics

If you only want to create your custom topics and skip the CMDB default topics:

```bash
KAFKA_CREATE_TOPICS="my-topic:3:2" KAFKA_SKIP_DEFAULT_TOPICS="true" docker-compose up -d
```

### Environment Variables Reference

| Variable | Type | Default | Description |
|----------|------|---------|-------------|
| `KAFKA_CREATE_TOPICS` | string | `""` | Comma-separated list of topics to create |
| `KAFKA_SKIP_DEFAULT_TOPICS` | string | `"false"` | Set to `"true"` to skip CMDB default topics |

---

---

## Configuration Details

### KRaft Configuration

Each broker is configured with:

```yaml
# Node identity
KAFKA_NODE_ID: "1"  # Unique for each broker
KAFKA_PROCESS_ROLES: "broker,controller"  # Both roles

# Quorum configuration (same for all brokers)
KAFKA_CONTROLLER_QUORUM_VOTERS: "1@kafka-broker-1:29092,2@kafka-broker-2:29092,3@kafka-broker-3:29092"

# Listeners
KAFKA_LISTENERS: "BROKER://:9092,CONTROLLER://:29092"
KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: "BROKER:PLAINTEXT,CONTROLLER:PLAINTEXT"
KAFKA_ADVERTISED_LISTENERS: "BROKER://localhost:9092"  # External access
KAFKA_INTER_BROKER_LISTENER_NAME: "BROKER"  # Internal communication

# Cluster
KAFKA_CLUSTER_ID: "cmdb-kafka-cluster"
```

### Why Each Broker Has Both Roles

| Role | Purpose | Required on All Brokers? |
|------|---------|--------------------------|
| `broker` | Handles client requests, stores data | ✅ Yes |
| `controller` | Manages cluster metadata, leadership election | ✅ Yes (for small clusters) |

For a **3-broker cluster**, it's recommended that all brokers have both roles. This ensures:
- ✅ High availability (if one broker fails, others can elect a new controller)
- ✅ Load balancing of controller responsibilities
- ✅ Simplicity in configuration

For **larger clusters** (5+ brokers), you might dedicate some brokers to only the `controller` role.

---

## Usage Commands

### Start/Stop Cluster

```bash
# Start cluster
docker-compose up -d

# Stop cluster (keeps data)
docker-compose down

# Stop cluster and remove volumes (WARNING: deletes all data!)
docker-compose down -v

# Restart cluster
docker-compose restart
```

### Cluster Management

```bash
# View cluster status
docker-compose ps

# View logs
docker-compose logs -f

# View broker 1 logs
docker-compose logs -f cmdb-kafka-1

# View Kafka UI logs
docker-compose logs -f cmdb-kafka-ui
```

### Topic Management via CLI

```bash
# List all topics
docker exec -it cmdb-kafka-1 kafka-topics --bootstrap-server localhost:9092 --list

# Describe a topic
docker exec -it cmdb-kafka-1 kafka-topics --bootstrap-server localhost:9092 --describe --topic cmdb.entity.events

# Create a new topic
docker exec -it cmdb-kafka-1 kafka-topics --bootstrap-server localhost:9092 --create \
  --topic my.new.topic \
  --partitions 3 \
  --replication-factor 2 \
  --config retention.ms=604800000

# Delete a topic
docker exec -it cmdb-kafka-1 kafka-topics --bootstrap-server localhost:9092 --delete --topic my.old.topic
```

### Message Operations

```bash
# Produce messages to a topic
docker exec -it cmdb-kafka-1 kafka-console-producer --bootstrap-server localhost:9092 --topic cmdb.entity.events

# Consume messages from beginning
docker exec -it cmdb-kafka-1 kafka-console-consumer --bootstrap-server localhost:9092 --topic cmdb.entity.events --from-beginning

# Follow new messages
docker exec -it cmdb-kafka-1 kafka-console-consumer --bootstrap-server localhost:9092 --topic cmdb.entity.events --follow
```

---

## Spring Boot Configuration

### application.yml for Local Development

```yaml
spring:
  kafka:
    # Connection to local Kafka cluster (use any broker)
    bootstrap-servers: localhost:9092,localhost:9093,localhost:9094
    
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
      batch-size: 16384
      linger-ms: 10
      buffer-memory: 33554432
      
      # KRaft specific: ensure idempotence
      enable-idempotence: true
      max-in-flight-requests-per-connection: 5
      
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      group-id: cmdb-${spring.application.name}
      auto-offset-reset: earliest
      enable-auto-commit: false
      session-timeout-ms: 10000
      heartbeat-interval-ms: 3000
      max-poll-records: 500
      
    listener:
      ack-mode: MANUAL_IMMEDIATE
      concurrency: 3
      poll-timeout: 3000

cmdb:
  kafka:
    topics:
      entity-events: cmdb.entity.events
      project-events: cmdb.project.events
      github-events: cmdb.aggregator.github
      syslog-events: cmdb.aggregator.syslog
      dlq: cmdb.dlq
```

### Producer Example

```java
@Service
public class EntityEventPublisher {

    @Value("${cmdb.kafka.topics.entity-events}")
    private String entityEventsTopic;

    private final KafkaTemplate<String, EntityEvent> kafkaTemplate;

    public void publishEntityCreated(EntityEvent event) {
        kafkaTemplate.send(entityEventsTopic, event.getEntityId().toString(), event);
    }
}
```

### Consumer Example

```java
@Component
public class EntityEventConsumer {

    @KafkaListener(
        topics = "${cmdb.kafka.topics.entity-events}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEntityEvent(EntityEvent event, Acknowledgment ack) {
        try {
            // Process event
            System.out.println("Received event: " + event.getType());
            ack.acknowledge();
        } catch (Exception e) {
            // Send to DLQ
            // kafkaTemplate.send(dlqTopic, event);
        }
    }
}
```

---

## Troubleshooting

### Cluster Not Starting

```bash
# Check container logs
docker-compose logs cmdb-kafka-1

# Common issues:
# 1. Port already in use -> Change ports in docker-compose.yml
# 2. Insufficient memory -> Increase Docker resources
# 3. Slow disk I/O -> Use faster storage for ./data directory
```

### Kafka UI Not Connecting

```bash
# Verify brokers are running
docker-compose ps

# Check Kafka UI logs
docker-compose logs cmdb-kafka-ui

# Test connection manually
docker exec -it cmdb-kafka-1 kafka-broker-api-versions --bootstrap-server localhost:9092
```

### Topics Not Created

```bash
# Check kafka-init logs
docker-compose logs cmdb-kafka-init

# Manually create topics
docker exec -it cmdb-kafka-1 /scripts/create-topics.sh

# Or create manually
docker exec -it cmdb-kafka-1 kafka-topics --bootstrap-server localhost:9092 --create --topic cmdb.entity.events --partitions 3 --replication-factor 2
```

### Connection Refused from Host

Make sure your application connects to the **external ports**:
- ✅ `localhost:9092` (Broker 1)
- ✅ `localhost:9093` (Broker 2)
- ✅ `localhost:9094` (Broker 3)

❌ NOT `kafka-broker-1:9092` (internal Docker network only)

---

## Data Persistence

All Kafka data is persisted in the `data/` directory:

```
infrastructure/kafka/
└── data/
    ├── kafka-1/    # Broker 1 data and logs
    ├── kafka-2/    # Broker 2 data and logs
    └── kafka-3/    # Broker 3 data and logs
```

To **completely reset** the cluster (delete all data):

```bash
# Stop containers
docker-compose down

# Remove data directories
rm -rf data/

# Restart
docker-compose up -d
```

---

## Scaling

### Reduce to 1 Broker (for development)

Edit `docker-compose.yml` and keep only `kafka-broker-1` and `kafka-ui`. Update:
- `KAFKA_CONTROLLER_QUORUM_VOTERS` to only include broker 1
- Remove brokers 2 and 3 from the file

### Add More Brokers (for production)

Add more broker services following the same pattern:
- Unique `KAFKA_NODE_ID`
- Unique external port
- Add to `KAFKA_CONTROLLER_QUORUM_VOTERS` list

---

## Useful Resources

- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [KRaft Documentation](https://kafka.apache.org/documentation/#kraft)
- [Kafka UI GitHub](https://github.com/provectuslabs/kafka-ui)
- [Apache Kafka Docker Image](https://hub.docker.com/r/apache/kafka)

---

## Version History

| Version | Kafka Version | KRaft | Changes |
|---------|---------------|-------|---------|
| 1.0 | 4.2.1 | ✅ Yes | Initial KRaft setup |
