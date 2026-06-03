#!/bin/bash

# ============================================================================
# Kafka Topics Initialization Script for CMDB
# This script creates all necessary topics when the cluster starts
# Supports KAFKA_CREATE_TOPICS environment variable for custom topics
# ============================================================================

set -e

echo "=========================================="
echo "  Creating CMDB Kafka Topics"
echo "=========================================="

# Bootstrap servers (using internal Docker DNS)
BOOTSTRAP_SERVERS="kafka-broker-1:9092,kafka-broker-2:9092,kafka-broker-3:9092"

# Read environment variables
CUSTOM_TOPICS="${KAFKA_CREATE_TOPICS:-}"
SKIP_DEFAULT="${KAFKA_SKIP_DEFAULT_TOPICS:-false}"

# Default retention (7 days in milliseconds)
DEFAULT_RETENTION_MS=604800000

# Function to check if Kafka is ready
wait_for_kafka() {
  local max_attempts=30
  local attempt=1
  
  echo "Waiting for Kafka cluster to be ready..."
  
  until kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --list &>/dev/null; do
    if [ $attempt -ge $max_attempts ]; then
      echo "ERROR: Kafka cluster did not become ready after $max_attempts attempts"
      exit 1
    fi
    echo "  Attempt $attempt/$max_attempts - Kafka not ready yet, waiting 5 seconds..."
    sleep 5
    attempt=$((attempt + 1))
  done
  
  echo "✅ Kafka cluster is ready!"
}

# Function to create a topic
# Usage: create_topic "topic_name" "partitions" "replication" "retention_ms"
create_topic() {
  local topic_name=$1
  local partitions=$2
  local replication=$3
  local retention_ms=$4
  
  echo ""
  echo "Creating topic: $topic_name"
  echo "  Partitions: $partitions"
  echo "  Replication Factor: $replication"
  echo "  Retention: $retention_ms ms ("$((retention_ms / 86400000))" days)"
  
  kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS \
    --create \
    --topic $topic_name \
    --partitions $partitions \
    --replication-factor $replication \
    --config retention.ms=$retention_ms \
    --if-not-exists
  
  echo "  ✅ Topic '$topic_name' created successfully!"
}

# Function to verify topic creation
verify_topic() {
  local topic_name=$1
  
  if kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --list | grep -q "^$topic_name$"; then
    echo "  ✅ Verified: Topic '$topic_name' exists"
  else
    echo "  ❌ ERROR: Topic '$topic_name' was not created!"
    exit 1
  fi
}

# Function to parse and create custom topics from KAFKA_CREATE_TOPICS
# Format: "topic1:partitions:replication[:retention_ms],topic2:partitions:replication[:retention_ms]"
create_custom_topics() {
  if [ -z "$CUSTOM_TOPICS" ]; then
    echo ""
    echo "----------------------------------------"
    echo "  No custom topics to create (KAFKA_CREATE_TOPICS is empty)"
    echo "----------------------------------------"
    return
  fi

  echo ""
  echo "----------------------------------------"
  echo "  Creating Custom Topics from KAFKA_CREATE_TOPICS"
  echo "----------------------------------------"
  echo "  KAFKA_CREATE_TOPICS=$CUSTOM_TOPICS"

  # Split by comma
  IFS=',' read -ra TOPIC_DEFS <<< "$CUSTOM_TOPICS"
  
  for topic_def in "${TOPIC_DEFS[@]}"; do
    # Split by colon
    IFS=':' read -ra PARTS <<< "$topic_def"
    
    local name="${PARTS[0]}"
    local partitions="${PARTS[1]:-3}"      # Default: 3 partitions
    local replication="${PARTS[2]:-2}"     # Default: 2 replication factor
    local retention_ms="${PARTS[3]:-$DEFAULT_RETENTION_MS}"  # Default: 7 days
    
    create_topic "$name" "$partitions" "$replication" "$retention_ms"
    verify_topic "$name"
  done
}

# Main execution
wait_for_kafka

# Create custom topics first (if any)
create_custom_topics

# Create CMDB default topics (unless skipped)
if [ "$SKIP_DEFAULT" = "true" ]; then
  echo ""
  echo "----------------------------------------"
  echo "  Skipping CMDB default topics (KAFKA_SKIP_DEFAULT_TOPICS=true)"
  echo "----------------------------------------"
else
  echo ""
  echo "----------------------------------------"
  echo "  Creating CMDB Core Topics"
  echo "----------------------------------------"

  # Entity events topic
  create_topic "cmdb.entity.events" 3 2 604800000
  verify_topic "cmdb.entity.events"

  # Project events topic
  create_topic "cmdb.project.events" 3 2 604800000
  verify_topic "cmdb.project.events"

  # Environment events topic
  create_topic "cmdb.environment.events" 3 2 604800000
  verify_topic "cmdb.environment.events"

  # Component events topic
  create_topic "cmdb.component.events" 3 2 604800000
  verify_topic "cmdb.component.events"

  echo ""
  echo "----------------------------------------"
  echo "  Creating Aggregator Topics"
  echo "----------------------------------------"

  # GitHub aggregator events
  create_topic "cmdb.aggregator.github" 3 2 604800000
  verify_topic "cmdb.aggregator.github"

  # Syslog aggregator events
  create_topic "cmdb.aggregator.syslog" 3 2 604800000
  verify_topic "cmdb.aggregator.syslog"

  echo ""
  echo "----------------------------------------"
  echo "  Creating System Topics"
  echo "----------------------------------------"

  # Dead Letter Queue (30 days retention)
  create_topic "cmdb.dlq" 3 2 2592000000
  verify_topic "cmdb.dlq"
fi

echo ""
echo "----------------------------------------"
echo "  Verifying All Topics"
echo "----------------------------------------"

ALL_TOPICS=$(kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --list)
echo "Current topics in cluster:"
echo "$ALL_TOPICS" | sed 's/^/  - /'

echo ""
echo "=========================================="
echo "  ✅ All Kafka topics created successfully!"
echo "=========================================="
echo ""
echo "Access Kafka UI at: http://localhost:8080"
echo "Bootstrap servers: localhost:9092, localhost:9093, localhost:9094"
