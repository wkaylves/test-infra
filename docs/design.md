# 统一测试基座架构说明

`test-common` 是团队测试基础设施库。它不替代 JUnit5、Spock、Mockito、Spring Test 或 Testcontainers，而是把团队常见测试场景沉淀成标准路径、默认配置、测试数据工具和外部依赖隔离能力。

当前架构以代码中的组件结构为准：**组件模块负责自己的测试入口，JUnit5 / Spock 模块只负责测试引擎级通用能力**。后续新增能力时应优先放入所属组件模块，而不是把能力集中到 `test-common-junit5` 或 `test-common-spock`。

## 1. 架构原则

### 1.1 按组件归属能力

Spring MVC、HTTP mock、MyBatis、存储、消息、RPC、调度等能力归属于各自组件模块。每个组件模块可以同时提供 JUnit5 与 Spock 入口。

示例：

- Controller 测试入口属于 `test-common-spring-mvc`。
- WireMock / Feign 测试入口属于 `test-common-http-mock`。
- MyBatis H2 / MySQL container 测试入口属于 `test-common-orm:test-common-mybatis`。
- Redis mock 与容器入口属于 `test-common-storage-nosql`。

### 1.2 测试引擎模块保持轻量

`test-common-junit5` 只承载纯 JUnit5 层面的通用扩展或约定。它不依赖 Spring MVC、MyBatis、Elasticsearch、MQ、RPC 等组件。

`test-common-spock` 只承载纯 Spock 层面的通用基类和 helper。组件级 Spring/Testcontainers 配置应留在组件模块。

### 1.3 纯 Mockito 不作为核心卖点

如果一个基类只是包装 `@ExtendWith(MockitoExtension.class)`，它的价值有限。文档中不把这类类作为主入口。只有当它沉淀了团队统一的 fixture、固定时间、断言、上下文 mock、数据 builder 或生命周期约定时，才提升为基座能力。

### 1.4 文档必须以真实代码为准

文档示例中的类名、模块名、注解和继承关系必须能在当前仓库中找到。禁止在 README 中提前使用尚未落地的目标态 API。

## 2. 模块拓扑

```text
test-common
├── build.gradle                         # 全局版本、依赖和发布配置
├── settings.gradle                      # 多模块声明
│
├── test-common-core/                    # 无测试引擎通用工具
│   └── com.github.kaylves.test.core
│       ├── TestData
│       ├── builder/                     # PageBuilder, ResultBuilder, Result
│       └── matcher/                     # JsonPathMatcher
│
├── test-common-junit5/                  # 纯 JUnit5 通用能力，保持轻量
│
├── test-common-spock/                   # 纯 Spock 通用能力
│   └── com.github.kaylves.test.spock
│       ├── BaseSpockSpec
│       └── BaseSpockRepositorySpec
│
├── test-common-spring-mvc/              # Spring MVC / Spring Boot 测试入口
│   └── com.github.kaylves.test.spring.mvc
│       ├── BaseServiceTest
│       ├── BaseControllerTest
│       ├── BaseIntegrationTest
│       ├── BaseServiceSpec
│       ├── BaseControllerSpec
│       └── BaseIntegrationSpec
│
├── test-common-orm/
│   ├── test-common-jpa/                 # JPA repository 测试入口
│   └── test-common-mybatis/             # MyBatis mapper 测试入口
│       ├── BaseH2MapperTest
│       ├── BaseMysqlContainerMapperTest
│       └── MockMyBatisUtils
│
├── test-common-storage-rdbms/           # MySQL / PostgreSQL / ClickHouse 容器
├── test-common-storage-nosql/           # Redis / MongoDB / Neo4j 容器与 mock
├── test-common-storage-search/          # Elasticsearch 容器
├── test-common-storage-file/            # MinIO / SFTP / FTP 测试基础设施
├── test-common-mq-broker/               # RabbitMQ / Kafka / RocketMQ / Pulsar
├── test-common-http-mock/               # WireMock / Feign / HTTP 依赖隔离
├── test-common-rpc-mock/                # Dubbo / gRPC mock
├── test-common-schedule-mock/           # XXL-Job / Quartz 哑火配置
├── test-common-all/                     # 聚合依赖，无业务能力
└── test-common-example/                 # 可运行样例和黄金路径
```

## 3. 组件职责契约

| 模块 | 负责 | 不负责 |
|------|------|--------|
| `test-common-core` | 通用测试数据、分页/响应构造、JSON 断言 | Spring、容器、中间件生命周期 |
| `test-common-junit5` | JUnit5 扩展、生命周期约定、引擎级 helper | Spring MVC、MyBatis、ES、MQ、RPC 入口 |
| `test-common-spock` | Spock 基类、Specification helper | 组件级 Spring/Testcontainers 配置 |
| `test-common-spring-mvc` | Controller、Service、Integration 的 Spring 测试入口 | 存储、MQ、RPC 组件生命周期 |
| `test-common-orm:test-common-mybatis` | MyBatis H2 slice、MySQL container mapper 测试、mapper mock | JPA repository 测试 |
| `test-common-orm:test-common-jpa` | JPA repository 测试入口 | MyBatis mapper 测试 |
| `test-common-storage-rdbms` | 关系型数据库 Testcontainers | Mapper 断言模板 |
| `test-common-storage-nosql` | NoSQL 容器和 Redis mock | 业务缓存策略 |
| `test-common-storage-search` | Elasticsearch 容器 | 搜索业务 DSL 封装 |
| `test-common-storage-file` | 文件/对象存储测试基础设施 | 业务文件处理流程 |
| `test-common-mq-broker` | MQ 容器和 RocketMQ mock | 业务消息幂等/事务策略 |
| `test-common-http-mock` | WireMock、Feign、RestTemplate HTTP mock | RPC/MQ mock |
| `test-common-rpc-mock` | Dubbo、gRPC mock 辅助 | HTTP mock |
| `test-common-schedule-mock` | 调度测试降噪和哑火配置 | Job 业务逻辑断言 |
| `test-common-all` | 依赖聚合 | 新增代码、基类或配置 |
| `test-common-example` | 样例和黄金路径 | 业务项目直接依赖 |

## 4. 推荐测试路径

### 4.1 Service 单测

Service 单测默认不启动 Spring 容器。纯 mock 场景推荐直接使用 JUnit5 + Mockito 或 Spock 原生能力。基座只提供通用测试数据、断言 helper 或中间件 mock 工具。

### 4.2 Controller 测试

Controller 测试使用 `test-common-spring-mvc`：

- JUnit5：`BaseControllerTest`
- Spock：`BaseControllerSpec`

### 4.3 Integration 测试

Spring Boot 集成测试使用 `test-common-spring-mvc`：

- JUnit5：`BaseIntegrationTest`
- Spock：`BaseIntegrationSpec`

组件环境由具体组件模块接入，例如数据库容器来自 `test-common-storage-rdbms`，HTTP mock 来自 `test-common-http-mock`。

### 4.4 Mapper / DAO 测试

MyBatis 测试使用 `test-common-orm:test-common-mybatis`：

- 快速 slice：`BaseH2MapperTest`
- 真实 MySQL 兼容性：`BaseMysqlContainerMapperTest`
- 纯 mock：`MockMyBatisUtils`

### 4.5 外部依赖隔离

- HTTP：`test-common-http-mock`
- MQ：`test-common-mq-broker`
- RPC：`test-common-rpc-mock`
- 调度：`test-common-schedule-mock`
- 存储：对应 `test-common-storage-*` 模块

## 5. 新增能力规则

新增测试能力时按以下顺序判断归属：

1. 是否是某个组件特有能力。是，则放入组件模块。
2. 是否同时适用于所有 JUnit5 测试且不引入组件依赖。是，才考虑 `test-common-junit5`。
3. 是否同时适用于所有 Spock 测试且不引入组件依赖。是，才考虑 `test-common-spock`。
4. 是否只是依赖聚合。是，放入 `test-common-all`，但不新增代码。
5. 是否只是示例。是，放入 `test-common-example`。

禁止为了“统一入口”把组件依赖集中塞入 `test-common-junit5` 或 `test-common-spock`。

## 6. 当前风险与后续改进

- `test-common-junit5` 当前能力较弱，文档中不作为主路径强调。
- `BaseServiceTest` 只是 Mockito 轻量入口，后续要么沉淀团队 Service 单测约定，要么继续弱化。
- 部分容器类使用全局单例和 System properties，后续应逐步向配置对象和 `DynamicPropertySource` 收敛。
- WireMock/Feign 当前存在静态状态和方法名级缓存，后续要评估并行测试和方法重载风险。
- `docs/review-history.md` 记录的是历史评审，不作为当前架构 API 说明。

## 7. 验收标准

- README 和架构文档中的类名都能在当前代码中找到。
- 每个推荐测试路径至少有一个可运行样例或测试。
- `test-common-junit5` 不被描述为 Spring MVC、Repository、Integration 的总入口。
- 组件模块职责清晰，新增能力能按本文件判断归属。
- `./gradlew testClasses --continue` 和 `./gradlew test --continue` 通过。
