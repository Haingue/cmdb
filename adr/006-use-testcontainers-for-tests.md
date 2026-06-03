# ADR-006: Use Testcontainers for Integration Testing

## Status

Accepted

## Context

The CMDB project needs comprehensive testing at multiple levels:

- **Unit tests**: Test individual classes and methods in isolation
- **Integration tests**: Test interactions between components (e.g., service + repository)
- **End-to-end tests**: Test the complete system with all dependencies

Current challenges with testing:
- **Mocking complexity**: Mocking Spring dependencies, JPA repositories, and external services is complex and error-prone
- **Real database behavior**: In-memory databases (H2) don't always behave the same as PostgreSQL
- **Test realism**: Mocks don't catch SQL syntax errors, constraint violations, or other database-specific issues
- **Environment parity**: "Works on my machine" problems due to different database versions/configured
- **Test data management**: Complex setup/teardown for test data

We need a solution that:
- Provides real database instances for testing
- Is isolated from the host environment
- Is fast to start and stop
- Is consistent across development, CI, and production environments
- Supports multiple database types (PostgreSQL, Redis, etc.)

## Decision

We adopt **Testcontainers** as our integration testing framework for testing with real dependencies.

### Testcontainers Setup

```java
// pom.xml dependencies
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.7</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.7</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.7</version>
    <scope>test</scope>
</dependency>
```

### Testing Patterns

#### 1. Database Integration Tests

```java
@Testcontainers
@SpringBootTest
class EntityRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private EntityRepository entityRepository;

    @Test
    void shouldSaveAndFindEntity() {
        // Given
        Entity entity = new Entity(UUID.randomUUID(), "Test Entity");

        // When
        entityRepository.save(entity);
        Entity found = entityRepository.findById(entity.getId()).orElseThrow();

        // Then
        assertThat(found.getName()).isEqualTo("Test Entity");
    }
}
```

#### 2. Service Integration Tests

```java
@Testcontainers
@SpringBootTest
class EntityServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private EntityService entityService;

    @Test
    void shouldCreateEntityWithComponents() {
        // Given
        CreateEntityCommand command = new CreateEntityCommand(
                "Test Entity",
                EntityType.HARDWARE,
                List.of(new ComponentData("CPU", "Intel i7"))
        );

        // When
        EntityDto result = entityService.createEntity(command);

        // Then
        assertThat(result.getName()).isEqualTo("Test Entity");
        assertThat(result.getComponents()).hasSize(1);
    }
}
```

#### 3. Multi-Container Tests (Service + Database + Kafka)

```java
@Testcontainers
@SpringBootTest
class EventIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer("confluentinc/cp-kafka:7.4.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Database
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        
        // Kafka
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private EntityService entityService;

    @Autowired
    private EventPublisher eventPublisher;

    @Test
    void shouldPublishEventOnEntityCreation() {
        // Given
        CreateEntityCommand command = new CreateEntityCommand(
                "Test Entity",
                EntityType.HARDWARE
        );

        // When
        entityService.createEntity(command);

        // Then - Verify event was published to Kafka
        // (This would use a test consumer or verify through repository)
    }
}
```

#### 4. Custom Container Images

For services that need to be tested in integration:

```java
public class InventoryServiceContainer extends GenericContainer<InventoryServiceContainer> {
    private static final int PORT = 8081;

    public InventoryServiceContainer() {
        super("cmdb-inventory-service:test");
        this.addExposedPort(PORT);
        this.addEnv("SPRING_PROFILES_ACTIVE", "test");
        this.addEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://host.docker.internal:5432/testdb");
    }

    public String getBaseUrl() {
        return "http://" + getHost() + ":" + getMappedPort(PORT);
    }
}
```

## Consequences

### Positive

- **Real dependencies**: Tests run against real PostgreSQL, Kafka, Redis, etc.
- **Catches bugs early**: Finds database-specific issues during development
- **Environment parity**: Same behavior in dev, CI, and production
- **No mocks**: Reduces complex mocking code
- **Fast feedback**: Containers start quickly (especially with reuse)
- **Isolated**: Each test gets its own clean environment
- **Flexible**: Can test any containerized service or database
- **CI compatible**: Works seamlessly in CI pipelines

### Negative

- **Slower tests**: Starting containers adds time to test runs
- **Resource intensive**: Requires Docker running on developer machines
- **CI resource usage**: CI runners need Docker and sufficient resources
- **Complex setup**: More complex than simple unit tests
- **Debugging**: Debugging container issues can be challenging

## Alternatives Considered

### 1. In-Memory Databases (H2)
- **Considered**: Fast, no dependencies
- **Rejected**: Doesn't match PostgreSQL behavior exactly
- **Rejected**: SQL dialect differences cause bugs in production
- **Rejected**: Missing features (JSON functions, CTEs, etc.)

### 2. Embedded Databases
- **Considered**: No Docker dependency
- **Considered**: Fast startup
- **Rejected**: Limited to specific database types
- **Rejected**: Still doesn't match production behavior exactly

### 3. Shared Test Database
- **Considered**: Single database instance for all tests
- **Rejected**: Tests interfere with each other
- **Rejected**: State pollution between test runs
- **Rejected**: Requires manual cleanup

### 4. Local Database Installation
- **Considered**: Use locally installed PostgreSQL
- **Rejected**: Environment-specific (different versions, configurations)
- **Rejected**: Requires all developers to have specific versions installed
- **Rejected**: "Works on my machine" problems persist

### 5. Mocking Frameworks (Mockito, WireMock)
- **Considered**: Fast, no dependencies
- **Rejected**: Doesn't catch integration issues
- **Rejected**: Complex mocking for Spring/Data JPA
- **Rejected**: Mocks can become outdated as code changes

## Best Practices

### 1. Container Reuse

Enable container reuse to speed up test runs:

```bash
# In ~/.testcontainers.properties
testcontainers.reuse.enable=true
```

Or programmatically:

```java
@BeforeAll
static void configureReuse() {
    System.setProperty("testcontainers.reuse.enable", "true");
}
```

### 2. Parallel Test Execution

Run tests in parallel with unique container names:

```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb-" + UUID.randomUUID());
```

### 3. Test Data Initialization

Use Flyway or Liquibase test migrations, or programmatic initialization:

```java
@BeforeEach
void setupTestData() {
    // Insert test data using JdbcTemplate or repository
    jdbcTemplate.execute("INSERT INTO entities ...");
}

@AfterEach
void cleanupTestData() {
    jdbcTemplate.execute("TRUNCATE TABLE entities CASCADE");
}
```

### 4. Custom Images for Services

Create Docker images for services that need to be tested:

```dockerfile
# Dockerfile.test
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/inventory-service.jar .
CMD ["java", "-jar", "inventory-service.jar"]
```

Build and publish:

```bash
# Build test image
docker build -t cmdb-inventory-service:test -f Dockerfile.test .

# Push to registry (optional)
docker push cmdb-inventory-service:test
```

### 5. CI Configuration

GitHub Actions example:

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    services:
      docker:
        image: docker:dind
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run integration tests
        run: mvn verify -Dtest=*IntegrationTest
```

### 6. Selective Test Execution

Use Maven profiles to separate unit and integration tests:

```xml
<!-- pom.xml -->
<profiles>
    <profile>
        <id>integration-tests</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <includes>
                            <include>**/*IntegrationTest.java</include>
                        </includes>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

Run integration tests:

```bash
mvn verify -Pintegration-tests
```

## Success Metrics

- All integration tests pass in development environment
- Integration tests run successfully in CI
- Test execution time is acceptable (< 5 minutes for full integration suite)
- No "works on my machine" issues related to database differences
- Database-specific bugs are caught before reaching production
- Test coverage includes integration scenarios, not just unit tests
