# test-infra

[中文](README.md) | English

Test Infrastructure Platform — providing standard paths, default configurations, test data utilities, and external dependency isolation for common team testing scenarios.

`test-infra` does not replace JUnit5, Spock, Mockito, Spring Test, or Testcontainers. Its responsibility is to codify team-approved testing practices into reusable components, making it easier to choose the right testing layer, isolate external dependencies, and maintain consistent assertion and data construction styles.

## Design Principles

- Component-based boundaries: Spring MVC, HTTP mock, MyBatis, storage, MQ, RPC, scheduling, etc. are provided by their respective modules.
- JUnit5 / Spock only provide test-engine-level generic capabilities; they don't carry all component scenarios.
- Pure Mockito scenarios prioritize native Mockito/JUnit5; only capabilities with team conventions enter the base.
- Documentation examples must correspond to real code; avoid misleading target-state APIs.
- `test-infra-all` only aggregates dependencies, without defining new capability boundaries.

## Module Overview

| Module | Responsibility | Usage |
|--------|---------------|-------|
| `test-infra-core` | PageBuilder, ResultBuilder, JsonPathMatcher, TestData and other common utilities | Common base dependency |
| `test-infra-junit5` | Pure JUnit5 level generic extensions or conventions | On demand |
| `test-infra-spock` | Spock base classes and common helpers | On demand |
| `test-infra-spring-mvc` | Spring MVC / Spring Boot test base classes, with JUnit5 and Spock entry points | Controller / Integration scenarios |
| `test-infra-orm:test-infra-mybatis` | MyBatis mapper testing, with H2 slice and MySQL container paths | Mapper/DAO scenarios |
| `test-infra-orm:test-infra-jpa` | JPA repository test entry point | Repository scenarios |
| `test-infra-storage-rdbms` | MySQL / PostgreSQL / ClickHouse Testcontainers | RDBMS integration tests |
| `test-infra-storage-nosql` | Redis / MongoDB / Neo4j containers and mock tools | NoSQL scenarios |
| `test-infra-storage-search` | Elasticsearch container support | Search scenarios |
| `test-infra-storage-file` | MinIO / SFTP / FTP test infrastructure | File/object storage scenarios |
| `test-infra-mq-broker` | RabbitMQ / Kafka / RocketMQ / Pulsar containers and mock tools | Messaging scenarios |
| `test-infra-http-mock` | WireMock, Feign, RestTemplate HTTP dependency isolation | External HTTP dependency scenarios |
| `test-infra-rpc-mock` | Dubbo / gRPC mock helpers | RPC scenarios |
| `test-infra-schedule-mock` | XXL-Job / Quartz test silence configuration | Scheduling scenarios |
| `test-infra-all` | One-click aggregation of all modules | Full integration |
| `test-infra-example` | Runnable examples and golden test paths | Reference only, not a direct dependency |

## Quick Start

### Choose Dependencies

```groovy
// Controller / Spring Boot integration tests
testImplementation 'io.github.wkaylves:test-infra-spring-mvc:1.0.0-SNAPSHOT'

// MyBatis mapper tests
testImplementation 'io.github.wkaylves:test-infra-mybatis:1.0.0-SNAPSHOT'

// External HTTP dependency isolation
testImplementation 'io.github.wkaylves:test-infra-http-mock:1.0.0-SNAPSHOT'

// Redis / RocketMQ mock tools
testImplementation 'io.github.wkaylves:test-infra-storage-nosql:1.0.0-SNAPSHOT'
testImplementation 'io.github.wkaylves:test-infra-mq-broker:1.0.0-SNAPSHOT'

// Full integration
testImplementation 'io.github.wkaylves:test-infra-all:1.0.0-SNAPSHOT'
```

### Service Unit Tests

Service unit tests stay lightweight by default, without starting the Spring container. Extending `BaseJUnit5Test` provides auto-injected AssertJ Soft Assertions, suitable for scenarios that verify multiple fields at once. Pure Mockito scenarios can still use native `@ExtendWith(MockitoExtension.class)` directly.

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest extends BaseJUnit5Test {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldFindOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("ORD-001");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.findOrder(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getOrderNo()).isEqualTo("ORD-001");
    }

    @Test
    void shouldVerifyMultipleFieldsSoftly() {
        Order order = new Order();
        order.setOrderNo("ORD-002");
        order.setCustomerName("Bob");

        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.createOrder(order);
        softly.assertThat(result.getOrderNo()).isEqualTo("ORD-002");
        softly.assertThat(result.getCustomerName()).isEqualTo("Bob");
    }
}
```

### Controller Tests

Controller tests belong to `test-infra-spring-mvc`, using `BaseControllerTest` and `MvcTestResult`.

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest extends BaseControllerTest {
    @Test
    void shouldReturnOrder() {
        MvcTestResult result = performGet("/api/orders/1");
        assertThat(result.assert2xx().readString("$.orderNo")).isEqualTo("ORD-001");
    }

    @Test
    void shouldCreateOrderWithHeader() {
        Map<String, String> headers = Collections.singletonMap("X-Trace-Id", "test-123");
        MvcTestResult result = performPost("/api/orders", requestBody, headers);
        assertThat(result.assertStatus(201).readString("$.orderNo")).isEqualTo("ORD-002");
    }
}
```

Spock entry point provided by the same component:

```groovy
@WebMvcTest(OrderController)
class OrderControllerSpec extends BaseControllerSpec {
    def "should return order"() {
        when:
        def result = performGet("/api/orders/1")

        then:
        result.assert2xx().readString('$.orderNo') == 'ORD-001'
    }

    def "should search with query params"() {
        when:
        def params = [keyword: 'java']
        def result = performGet("/api/orders/search", null, params)

        then:
        result.readString('$.keyword') == 'java'
    }
}
```

### MyBatis Mapper Tests

Mapper tests belong to `test-infra-orm:test-infra-mybatis`. Quick path uses H2 slice:

```java
@BaseH2MapperTest
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldSelectById() {
        assertThat(userMapper.findById(1L)).isNotNull();
    }
}
```

Use the container path when real MySQL compatibility is needed:

```java
class UserMapperMysqlTest extends BaseMysqlContainerMapperTest {
    @Autowired
    private UserMapper userMapper;
}
```

### HTTP Mock

External HTTP dependency isolation belongs to `test-infra-http-mock`.

```java
class MyFeignTest extends WireMockTestBase {
    @Override
    protected void setupStubs() {
        stubGet("/api/external/users")
                .queryParam("status", "ACTIVE")
                .requestHeader("X-Trace-Id", "test-trace")
                .header("X-Mock", "wiremock")
                .body("[{\"id\":1,\"name\":\"Alice\"}]")
                .stub();
    }

    @Test
    void shouldCallExternalApi() {
        String baseUrl = getBaseUrl();
        // Point client to baseUrl and make the call
    }
}
```

Spock entry point:

```groovy
class MyFeignSpec extends BaseWireMockSpec {
    def setupSpec() {
        stubGet('/api/external/ping')
                .textBody('pong')
                .contentType('text/plain')
                .stub()
    }

    def "should call external api"() {
        expect:
        getBaseUrl()
    }
}
```

For Feign convenience entry, use `willReturn(Client.class, "methodName")` to preset the response for the next call. If the Feign interface has overloaded methods, use `willReturn(Client.class, Method)` — the method name entry will fail fast to avoid incorrect binding by string method name.

### Middleware Mock

Common middleware mocks are provided as utility classes, avoiding inheritance just for mocking.

```java
RedisTemplate<String, Object> redisTemplate = MockRedisUtils.mockRedisTemplate();
RocketMQTemplate mqTemplate = MockRocketMQUtils.mockRocketMQTemplate();
OrderMapper mapper = MockMyBatisUtils.mockMapper(OrderMapper.class);
```

### Schedule Silencing

Schedule tests belong to `test-infra-schedule-mock`.

```java
@XxlJobTestBase
class MyXxlJobTest {
    // XXL-Job admin address set to empty, Quartz auto-startup=false
}
```

## Core Utility Classes

| Class | Module | Description |
|-------|--------|-------------|
| `PageBuilder` | core | Build paginated test data |
| `ResultBuilder` | core | Build unified response test data |
| `JsonPathMatcher` | core | JSON response assertions |
| `TestData` | core | Common Map test data |
| `BaseJUnit5Test` | junit5 | JUnit5 test base (AssertJ Soft Assertions auto-injection) |
| `BaseControllerTest` | spring-mvc | JUnit5 MockMvc shortcut |
| `BaseControllerSpec` | spring-mvc | Spock MockMvc shortcut |
| `MvcTestResult` | spring-mvc | MockMvc response wrapper (HTTP status + JsonPathMatcher + assertion chain) |
| `MvcTestException` | spring-mvc | MockMvc request failure runtime exception |
| `BaseIntegrationTest` | spring-mvc | Spring Boot integration test annotation |
| `BaseIntegrationSpec` | spring-mvc | Spock Spring Boot integration test entry |
| `BaseH2MapperTest` | mybatis | MyBatis + H2 slice test annotation |
| `BaseMysqlContainerMapperTest` | mybatis | MyBatis + MySQL Testcontainers base class |
| `MockRedisUtils` | storage-nosql | Redis mock utility |
| `MockRocketMQUtils` | mq-broker | RocketMQ mock utility |
| `MockMyBatisUtils` | mybatis | MyBatis mapper mock utility |
| `SharedMySQLContainer` | storage-rdbms | MySQL singleton container |
| `SharedPostgreSQLContainer` | storage-rdbms | PostgreSQL singleton container |
| `SharedClickHouseContainer` | storage-rdbms | ClickHouse singleton container |
| `SharedRedisContainer` | storage-nosql | Redis singleton container |
| `SharedMongoDBContainer` | storage-nosql | MongoDB singleton container |
| `SharedNeo4jContainer` | storage-nosql | Neo4j singleton container |
| `SharedElasticsearchContainer` | storage-search | Elasticsearch singleton container |
| `SharedMinIOContainer` | storage-file | MinIO singleton container |
| `SharedRabbitMQContainer` | mq-broker | RabbitMQ singleton container |
| `SharedKafkaContainer` | mq-broker | Kafka singleton container |
| `SharedRocketMQContainer` | mq-broker | RocketMQ singleton container |
| `SharedPulsarContainer` | mq-broker | Pulsar singleton container |
| `WireMockTestBase` | http-mock | JUnit5 WireMock test base class |
| `BaseWireMockSpec` | http-mock | Spock WireMock test base class |
| `WireMockStubBuilder` | http-mock | WireMock Stub builder |
| `DubboTestBase` | rpc-mock | Dubbo mock base class |
| `GrpcTestBase` | rpc-mock | gRPC mock base class |

## Tech Stack

- Java 8+
- Gradle 8.8 wrapper
- Spring Boot 2.7.18
- JUnit 5.9.3 / Mockito 4.11.0 / AssertJ 3.24.2
- Spock 2.3 / Groovy 4.0
- Testcontainers 1.19.3
- WireMock 2.35.1

## Build

```bash
# Compile test code
./gradlew testClasses --continue

# Run tests
./gradlew test --continue

# Publish to GitHub Packages
./gradlew publish

# Publish to Maven Central
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```

## Maven Central Publishing

This project supports publishing to Maven Central. For detailed configuration and usage instructions, refer to [MAVEN_CENTRAL_PUBLISHING.md](MAVEN_CENTRAL_PUBLISHING.md).

**Quick Start:**

1. Generate a Central Portal token at https://central.sonatype.com/usertoken
2. Copy `gradle.properties.template` to `gradle.properties` and fill in the token plus signing key
3. Run `./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository`

> Spring Boot 2.7.x reached EOL in November 2023. Upgrading to 3.x requires Java 17+ and Jakarta namespace migration; this library has not declared Spring Boot 3.x compatibility.
