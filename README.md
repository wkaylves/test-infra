# test-infra

[English](README_EN.md)

统一测试基座（Test Infrastructure Platform）—— 为团队常见测试场景提供标准路径、默认配置、测试数据工具和外部依赖隔离能力。

`test-infra` 不替代 JUnit5、Spock、Mockito、Spring Test 或 Testcontainers。它的职责是把团队认可的测试写法沉淀成可复用组件，让新增测试用例时更容易选对测试层次、隔离外部依赖，并保持一致的断言与数据构造风格。

## 设计原则

- 以组件为边界：Spring MVC、HTTP mock、MyBatis、存储、MQ、RPC、调度等能力由各自模块提供。
- JUnit5 / Spock 只提供测试引擎级通用能力，不承载所有组件场景。
- 纯 Mockito 场景优先使用原生 Mockito/JUnit5；只有沉淀了团队约定的能力才进入基座。
- 文档示例必须对应真实代码，避免目标态 API 误导接入。
- `test-infra-all` 只做聚合依赖，不定义新的能力边界。

## 模块总览

| 模块 | 职责 | 接入方式 |
|------|------|---------|
| `test-infra-core` | PageBuilder、ResultBuilder、JsonPathMatcher、TestData 等通用工具 | 常用基础依赖 |
| `test-infra-junit5` | 纯 JUnit5 层面的通用扩展或约定 | 按需 |
| `test-infra-spock` | Spock 基类和通用 helper | 按需 |
| `test-infra-spring-mvc` | Spring MVC / Spring Boot 测试基类，含 JUnit5 与 Spock 入口 | Controller / Integration 场景 |
| `test-infra-orm:test-infra-mybatis` | MyBatis mapper 测试，含 H2 slice 与 MySQL container 路径 | Mapper/DAO 场景 |
| `test-infra-orm:test-infra-jpa` | JPA repository 测试入口 | Repository 场景 |
| `test-infra-storage-rdbms` | MySQL / PostgreSQL / ClickHouse Testcontainers | 关系型数据库集成测试 |
| `test-infra-storage-nosql` | Redis / MongoDB / Neo4j 容器与 mock 工具 | NoSQL 场景 |
| `test-infra-storage-search` | Elasticsearch 容器支持 | 搜索场景 |
| `test-infra-storage-file` | MinIO / SFTP / FTP 测试基础设施 | 文件/对象存储场景 |
| `test-infra-mq-broker` | RabbitMQ / Kafka / RocketMQ / Pulsar 容器与 mock 工具 | 消息场景 |
| `test-infra-http-mock` | WireMock、Feign、RestTemplate 等 HTTP 依赖隔离 | 外部 HTTP 依赖场景 |
| `test-infra-rpc-mock` | Dubbo / gRPC mock 辅助 | RPC 场景 |
| `test-infra-schedule-mock` | XXL-Job / Quartz 测试哑火配置 | 调度场景 |
| `test-infra-all` | 一键聚合所有模块 | 全量接入 |
| `test-infra-example` | 可运行样例和黄金测试路径 | 参考，不直接依赖 |

## 快速开始

### 选择依赖

```groovy
// Controller / Spring Boot 集成测试
testImplementation 'io.github.wkaylves:test-infra-spring-mvc:1.0.0-SNAPSHOT'

// MyBatis mapper 测试
testImplementation 'io.github.wkaylves:test-infra-mybatis:1.0.0-SNAPSHOT'

// 外部 HTTP 依赖隔离
testImplementation 'io.github.wkaylves:test-infra-http-mock:1.0.0-SNAPSHOT'

// Redis / RocketMQ mock 工具
testImplementation 'io.github.wkaylves:test-infra-storage-nosql:1.0.0-SNAPSHOT'
testImplementation 'io.github.wkaylves:test-infra-mq-broker:1.0.0-SNAPSHOT'

// 全量接入
testImplementation 'io.github.wkaylves:test-infra-all:1.0.0-SNAPSHOT'
```

### Service 单测

Service 单测默认保持轻量，不启动 Spring 容器。继承 `BaseJUnit5Test` 可获得 AssertJ Soft Assertions 自动注入，适合需要一次验证多个字段的场景。纯 Mockito 场景仍可直接使用原生 `@ExtendWith(MockitoExtension.class)`。

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

### Controller 测试

Controller 测试归属 `test-infra-spring-mvc`，使用 `BaseControllerTest` 和 `MvcTestResult`。

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

Spock 入口由同一组件提供：

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

### MyBatis Mapper 测试

Mapper 测试归属 `test-infra-orm:test-infra-mybatis`。快速路径使用 H2 slice：

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

需要真实 MySQL 兼容性时使用容器路径：

```java
class UserMapperMysqlTest extends BaseMysqlContainerMapperTest {
    @Autowired
    private UserMapper userMapper;
}
```

### HTTP Mock

外部 HTTP 依赖隔离归属 `test-infra-http-mock`。

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
        // client 指向 baseUrl 后发起调用
    }
}
```

Spock 入口：

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

需要 Feign 便捷入口时，可以用 `willReturn(Client.class, "methodName")` 预置下一次调用的响应。若 Feign 接口存在重载方法，使用 `willReturn(Client.class, Method)`，方法名入口会 fail fast，避免按字符串方法名误绑定。

### 中间件 Mock

常见中间件 mock 以工具类方式提供，避免为了 mock 继承基类。

```java
RedisTemplate<String, Object> redisTemplate = MockRedisUtils.mockRedisTemplate();
RocketMQTemplate mqTemplate = MockRocketMQUtils.mockRocketMQTemplate();
OrderMapper mapper = MockMyBatisUtils.mockMapper(OrderMapper.class);
```

### 调度哑火

调度测试归属 `test-infra-schedule-mock`。

```java
@XxlJobTestBase
class MyXxlJobTest {
    // XXL-Job admin 地址置空，Quartz auto-startup=false
}
```

## 核心工具类

| 类 | 模块 | 说明 |
|----|------|------|
| `PageBuilder` | core | 构造分页测试数据 |
| `ResultBuilder` | core | 构造统一响应测试数据 |
| `JsonPathMatcher` | core | JSON 响应断言 |
| `TestData` | core | 常见 Map 测试数据 |
| `BaseJUnit5Test` | junit5 | JUnit5 测试基座（AssertJ Soft Assertions 自动注入） |
| `BaseControllerTest` | spring-mvc | JUnit5 MockMvc 快捷入口 |
| `BaseControllerSpec` | spring-mvc | Spock MockMvc 快捷入口 |
| `MvcTestResult` | spring-mvc | MockMvc 响应包装（含 HTTP 状态码 + JsonPathMatcher + 断言链） |
| `MvcTestException` | spring-mvc | MockMvc 请求失败运行时异常 |
| `BaseIntegrationTest` | spring-mvc | Spring Boot 集成测试注解 |
| `BaseIntegrationSpec` | spring-mvc | Spock Spring Boot 集成测试入口 |
| `BaseH2MapperTest` | mybatis | MyBatis + H2 slice 测试注解 |
| `BaseMysqlContainerMapperTest` | mybatis | MyBatis + MySQL Testcontainers 基类 |
| `MockRedisUtils` | storage-nosql | Redis mock 工具 |
| `MockRocketMQUtils` | mq-broker | RocketMQ mock 工具 |
| `MockMyBatisUtils` | mybatis | MyBatis mapper mock 工具 |
| `SharedMySQLContainer` | storage-rdbms | MySQL 单例容器 |
| `SharedPostgreSQLContainer` | storage-rdbms | PostgreSQL 单例容器 |
| `SharedClickHouseContainer` | storage-rdbms | ClickHouse 单例容器 |
| `SharedRedisContainer` | storage-nosql | Redis 单例容器 |
| `SharedMongoDBContainer` | storage-nosql | MongoDB 单例容器 |
| `SharedNeo4jContainer` | storage-nosql | Neo4j 单例容器 |
| `SharedElasticsearchContainer` | storage-search | Elasticsearch 单例容器 |
| `SharedMinIOContainer` | storage-file | MinIO 单例容器 |
| `SharedRabbitMQContainer` | mq-broker | RabbitMQ 单例容器 |
| `SharedKafkaContainer` | mq-broker | Kafka 单例容器 |
| `SharedRocketMQContainer` | mq-broker | RocketMQ 单例容器 |
| `SharedPulsarContainer` | mq-broker | Pulsar 单例容器 |
| `WireMockTestBase` | http-mock | JUnit5 WireMock 测试基类 |
| `BaseWireMockSpec` | http-mock | Spock WireMock 测试基类 |
| `WireMockStubBuilder` | http-mock | WireMock Stub 构建器 |
| `DubboTestBase` | rpc-mock | Dubbo mock 基类 |
| `GrpcTestBase` | rpc-mock | gRPC mock 基类 |

## 技术栈

- Java 8+
- Gradle 8.8 wrapper
- Spring Boot 2.7.18
- JUnit 5.9.3 / Mockito 4.11.0 / AssertJ 3.24.2
- Spock 2.3 / Groovy 4.0
- Testcontainers 1.19.3
- WireMock 2.35.1

## 构建

```bash
# 编译测试代码
./gradlew testClasses --continue

# 运行测试
./gradlew test --continue

# 发布到 GitHub Packages
./gradlew publish
```

> Spring Boot 2.7.x 已于 2023 年 11 月 EOL。升级到 3.x 需要 Java 17+ 和 Jakarta 命名空间迁移，本库尚未声明 Spring Boot 3.x 兼容性。
