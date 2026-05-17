# test-common

统一测试基座（Test Infrastructure Platform）—— 解决开发人员"不会写、不想写"单元测试的核心痛点。

采用 **"核心抽象 + 插拔式 Starter + 分层模板"** 的矩阵式架构，完美兼容 JUnit 5 与 Spock，实现类似 Spring Boot Starter 的独立、静默中间件测试管理。

## 模块总览

| 模块 | 说明 | 按需引入 |
|------|------|---------|
| `test-common-core` | 核心抽象：PageBuilder、ResultBuilder、JsonPathMatcher | 必须 |
| `test-common-junit5` | JUnit5 测试基类（Service / Controller / Repository / Integration） | 二选一 |
| `test-common-spock` | Spock BDD 测试基类 | 二选一 |
| `test-common-storage-rdbms` | MySQL / PostgreSQL / ClickHouse 容器 + Mock | 按需 |
| `test-common-storage-nosql` | Redis / MongoDB / Neo4j 容器 + Mock | 按需 |
| `test-common-storage-search` | Elasticsearch 容器 | 按需 |
| `test-common-storage-file` | MinIO / SFTP / FTP 测试基类 | 按需 |
| `test-common-mq-broker` | RabbitMQ / Kafka / RocketMQ / Pulsar 容器 + Mock | 按需 |
| `test-common-http-mock` | WireMock（Feign / RestTemplate / WebClient 拦截） | 按需 |
| `test-common-rpc-mock` | Dubbo / gRPC Mock | 按需 |
| `test-common-schedule-mock` | XXL-Job / Quartz 哑火 | 按需 |
| `test-common-all` | 一键引入所有模块 | 全都要 |
| `test-common-example` | 黄金测试样板间 | 参考 |

## 快速开始

### 引入依赖

```groovy
// Service 层单测：JUnit5 + Redis mock + RocketMQ mock
testImplementation 'com.github.kaylves:test-common-junit5:1.0.0-SNAPSHOT'
testImplementation 'com.github.kaylves:test-common-storage-nosql:1.0.0-SNAPSHOT'
testImplementation 'com.github.kaylves:test-common-mq-broker:1.0.0-SNAPSHOT'

// DAO 层集成测试：Spock + MySQL
testImplementation 'com.github.kaylves:test-common-spock:1.0.0-SNAPSHOT'
testImplementation 'com.github.kaylves:test-common-storage-rdbms:1.0.0-SNAPSHOT'

// 全都要
testImplementation 'com.github.kaylves:test-common-all:1.0.0-SNAPSHOT'
```

### Service 层测试（JUnit5）

```java
class MyServiceTest extends BaseServiceTestBase {
    @Mock private MyRepository repo;
    @InjectMocks private MyService service;

    @Test
    void test() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Order()));
        assertThat(service.findOrder(1L)).isPresent();
    }
}
```

### Controller 层测试（JUnit5）

```java
@BaseIntegrationTestBase
class MyControllerTest extends BaseControllerTestBase {
    @Test
    void test() throws Exception {
        JsonPathMatcher result = performGetAndMatch("/api/orders/1");
        assertThat(result.readString("$.orderNo")).isEqualTo("ORD-001");
    }
}
```

### Spock BDD 测试

```groovy
class MySpec extends BaseSpockSpec {
    @Mock OrderRepository orderRepository
    @InjectMocks OrderService orderService

    def setup() {
        MockitoAnnotations.openMocks(this)
    }

    def "should create order"() {
        given:
        def order = new Order()
        order.setOrderNo("ORD-001")

        when(orderRepository.save(any())).thenReturn(order)

        when:
        def result = orderService.createOrder(order)

        then:
        result.getOrderNo() == "ORD-001"
    }
}
```

### 中间件 Mock（组合式，无需继承基类）

```java
// Redis Mock
RedisTemplate<String, Object> redisTemplate = MockRedisUtils.mockRedisTemplate();

// RocketMQ Mock
RocketMQTemplate mqTemplate = MockRocketMQUtils.mockRocketMQTemplate();

// MyBatis Mapper Mock
OrderMapper mapper = MockMyBatisUtils.mockMapper(OrderMapper.class);
```

### Testcontainers 集成测试

```java
@BaseIntegrationTestBase
class MyIntegrationTest {
    @ClassRule
    public static SharedMySQLContainer mysql = SharedMySQLContainer.getInstance();

    @Test
    void testWithRealMySQL() {
        // 自动注入动态端口到 System Properties
    }
}
```

### HTTP Mock（WireMock）

```java
class MyFeignTest extends WireMockTestBase {
    @Override
    protected void setupStubs() {
        WireMockStubBuilder.on(wireMockServer)
                .get("/api/external/users")
                .withStatus(200)
                .withBody("[{\"id\":1,\"name\":\"Alice\"}]")
                .stub();
    }

    @Test
    void test() {
        // Feign/RestTemplate 请求被 WireMock 拦截
    }
}
```

### 调度哑火

```java
@XxlJobTestBase
class MyXxlJobTest {
    // XXL-Job 自动哑火，Quartz 自动停止，不干扰测试
}
```

## 核心工具类

| 类 | 模块 | 说明 |
|----|------|------|
| `PageBuilder` | core | 构造分页测试数据 |
| `ResultBuilder` | core | 构造统一响应测试数据 |
| `JsonPathMatcher` | core | JSON 响应断言 |
| `MockRedisUtils` | storage-nosql | Redis mock 工具 |
| `MockRocketMQUtils` | mq-broker | RocketMQ mock 工具 |
| `MockMyBatisUtils` | storage-rdbms | MyBatis mock 工具 |
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
| `WireMockTestBase` | http-mock | WireMock 测试基类 |
| `WireMockStubBuilder` | http-mock | WireMock Stub 构建器 |
| `DubboTestBase` | rpc-mock | Dubbo Mock 基类 |
| `GrpcTestBase` | rpc-mock | gRPC Mock 基类 |

## 技术栈

- Java 8+
- Gradle 7.3+
- Spring Boot 2.7.18
- JUnit 5.9.3 / Mockito 4.11.0 / AssertJ 3.24.2
- Spock 2.3 / Groovy 4.0
- Testcontainers 1.19.3
- WireMock 2.35.1

## 构建

```bash
# 编译
gradle build -x test

# 运行测试
gradle test

# 发布到 GitHub Packages
gradle publish
```

> **注意**: Spring Boot 2.7.x 已于 2023 年 11 月 EOL。
> 升级到 3.x 需要 Java 17+ 和 Jakarta 命名空间迁移。
> 本库尚未测试 Spring Boot 3.x 兼容性。
