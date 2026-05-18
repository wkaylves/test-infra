# test-common 文档与职责收敛计划

## 背景

当前代码结构已经更适合按组件提供测试能力，而不是把 JUnit5 或 Spock 做成覆盖所有场景的大入口。文档需要以现有代码为准，明确每个组件的职责边界，避免后续维护者或 AI 继续按旧文档把能力堆到 `test-common-junit5`。

## 核心决策

- `test-common` 不替代 JUnit5、Spock、Mockito、Spring Test、Testcontainers。
- `test-common` 的目标是统一团队常见测试场景的标准路径、默认配置、测试数据工具和外部依赖隔离方式。
- 组件能力归属组件模块。Spring MVC、HTTP mock、MyBatis、存储、MQ、RPC、调度等能力不下沉到 `test-common-junit5` 或 `test-common-spock`。
- `test-common-junit5` 和 `test-common-spock` 只承载测试引擎级通用能力；如果只是包一层 Mockito，不作为核心卖点。
- `test-common-all` 只做聚合依赖，不能反向定义架构边界。

## 组件职责

| 模块 | 职责 | 不负责 |
|------|------|--------|
| `test-common-core` | 通用测试数据、分页/响应构造、JSON 断言等无测试引擎工具 | Spring、容器、中间件生命周期 |
| `test-common-junit5` | 纯 JUnit5 层面的通用扩展或约定 | Spring MVC、MyBatis、ES、MQ、RPC 等组件入口 |
| `test-common-spock` | 纯 Spock 层面的通用基类和 helper | 组件级 Spring/Testcontainers 配置 |
| `test-common-spring-mvc` | Controller、Service、Integration 的 Spring MVC/JUnit5/Spock 支持 | 存储、消息、RPC 组件生命周期 |
| `test-common-orm:test-common-mybatis` | MyBatis mapper 测试入口，包含 H2 slice 和 MySQL container 路径 | JPA、通用关系型容器聚合 |
| `test-common-orm:test-common-jpa` | JPA repository 测试入口 | MyBatis mapper 入口 |
| `test-common-storage-rdbms` | MySQL、PostgreSQL、ClickHouse Testcontainers | Mapper 测试模板 |
| `test-common-storage-nosql` | Redis、MongoDB、Neo4j 容器与 mock 工具 | 业务缓存策略 |
| `test-common-storage-search` | Elasticsearch 容器支持 | 搜索业务断言模板 |
| `test-common-storage-file` | MinIO、SFTP、FTP 测试基础设施 | 业务文件处理流程 |
| `test-common-mq-broker` | RabbitMQ、Kafka、RocketMQ、Pulsar 容器与 mock 工具 | 业务消息幂等/事务策略 |
| `test-common-http-mock` | WireMock、Feign、RestTemplate 等 HTTP 依赖隔离 | RPC、MQ、真实外部环境 |
| `test-common-rpc-mock` | Dubbo、gRPC mock 辅助 | HTTP mock |
| `test-common-schedule-mock` | XXL-Job、Quartz 测试降噪/哑火配置 | 任务业务逻辑断言 |
| `test-common-all` | 聚合依赖，方便全量接入 | 新增业务能力或基类 |
| `test-common-example` | 可运行样例和黄金测试路径 | 被业务项目直接依赖 |

## 执行步骤

1. 更新 README：按测试场景组织接入方式，移除不存在或不准确的类名。
2. 更新架构文档：以组件职责和依赖边界为主，不再描述 JUnit5 大入口。
3. 更新评审清单：加入模块职责、文档示例可运行性、组件内双入口等检查项。
4. 保持代码不变，先让文档成为后续实现和 AI 维护的准绳。
5. 文档更新后运行 `./gradlew testClasses --continue` 和 `./gradlew test --continue` 验证仓库状态。

## 验收标准

- README 中所有推荐类名都能在当前代码中找到。
- README 不再把 `test-common-junit5` 描述成 Spring MVC、Repository、Integration 的总入口。
- 架构文档明确“组件模块提供自己的 JUnit5/Spock 支持”。
- 评审清单包含“组件职责不可越界”的检查项。
- Gradle 编译和测试通过。
