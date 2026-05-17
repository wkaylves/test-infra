# 统一测试基座（Test Infrastructure Platform）架构方案说明书

大厂在推进效能提升时，构建统一的测试基座通常是核心破局点。开发人员“不会写、不想写”的核心原因在于配置成本高、环境不稳定、缺乏标准范式。本方案采用**“核心抽象 + 插拔式 Starter + 分层模板”**的矩阵式架构设计，完美兼容 JUnit 5 与 Spock，并实现类似 Spring Boot Starter 的独立、静默中间件测试管理。

---

## 1. 整体架构拓扑与模块设计

为了避免模块过度碎片化带来的维护地狱（Maintenance Hell），同时利用 Spring Boot AutoConfiguration 强大的“条件装配”特性，我们将原本繁琐的 30+ 组件浓缩合并为以下 **10 个核心模块**。

```text
test-common (全局版本管理)
├── build.gradle (管理全局依赖版本 ext)
├── settings.gradle
│
├── test-common-core/                    # 🔵 核心抽象与通用 Matcher (纯Java，无测试引擎)
│   └── com.github.kaylves.test.core
│       ├── builder/                     # PageBuilder, ResultBuilder, Result
│       └── matcher/                     # JsonPathMatcher
│
├── test-common-junit5/                  # 🟢 JUnit5 场景切片与测试基类
│   └── com.github.kaylves.test.junit5
│       ├── BaseServiceTestBase
│       ├── BaseControllerTestBase
│       ├── BaseRepositoryTestBase
│       └── BaseIntegrationTestBase
│
├── test-common-spock/                   # 🟠 Spock 场景切片与测试基类
│   └── com.github.kaylves.test.spock
│       ├── BaseSpockSpec
│       └── BaseSpockRepositorySpec
│
│ ── 📦 状态与存储服务（基于 Testcontainers 自动拉起） ──
│
├── test-common-storage-rdbms/           # 关系型数据库 (MySQL / PostgreSQL / ClickHouse)
│   └── com.github.kaylves.test.storage.rdbms
│
├── test-common-storage-nosql/           # NoSQL 缓存与图数据库 (Redis / MongoDB / Neo4j)
│   └── com.github.kaylves.test.storage.nosql
│
├── test-common-storage-search/          # 搜索引擎 (Elasticsearch)
│   └── com.github.kaylves.test.storage.elasticsearch
│
├── test-common-storage-file/            # 文件与对象存储 (MinIO / SFTP / FTP)
│   └── com.github.kaylves.test.file
│
│ ── 📨 异步消息队列 ───────────────────────────
│
├── test-common-mq-broker/               # 消息中间件 (RabbitMQ / Kafka / RocketMQ / Pulsar)
│   └── com.github.kaylves.test.mq
│
│ ── 🌐 流量拦截与 RPC ──────────────────────────
│
├── test-common-http-mock/               # 基于 WireMock 拦截所有 Feign / RestTemplate / WebClient
│   └── com.github.kaylves.test.http.wiremock
│
├── test-common-rpc-mock/                # 针对 Dubbo / gRPC 的专属流量劫持拦截器
│   └── com.github.kaylves.test.rpc
│
│ ── ⏰ 任务调度降级 ───────────────────────────
│
├── test-common-schedule-mock/           # 自动使 XXL-Job / Quartz 哑火，避免干扰测试
│   └── com.github.kaylves.test.schedule
│
│ ── ⚪ 聚合与门面 ─────────────────────────────
│
├── test-common-all/                     # 一键引入所有模块（仅依赖聚合，无代码）
└── test-common-example/                 # 黄金测试样板间
    └── com.github.kaylves.test.example