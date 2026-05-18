# AGENTS.md

## graphify

- **graphify** (`~/.Codex/skills/graphify/SKILL.md`) - any input to knowledge graph. Trigger: `/graphify`
- When the user types `/graphify`, invoke the Skill tool with `skill: "graphify"` before doing anything else.

## test-common 维护约束

- 以当前代码结构为准维护文档和实现，禁止按旧目标态 API 编写示例。
- `test-common` 的目标是统一团队常见测试场景的标准路径、默认配置、测试数据工具和外部依赖隔离能力。
- `test-common` 不替代 JUnit5、Spock、Mockito、Spring Test 或 Testcontainers。
- 组件能力归属组件模块：Spring MVC、HTTP mock、MyBatis、存储、MQ、RPC、调度等能力不要集中塞入 `test-common-junit5` 或 `test-common-spock`。
- `test-common-junit5` 只承载纯 JUnit5 层面的通用能力；`test-common-spock` 只承载纯 Spock 层面的通用能力。
- `test-common-all` 只做依赖聚合，不新增基类、工具类或配置。
- 纯 Mockito 包装不作为核心卖点；除非沉淀了团队 fixture、固定时间、上下文 mock、断言或生命周期约定，否则优先推荐原生 JUnit5 + Mockito。
- 新增或修改 README 示例前，必须确认类名、注解、模块名能在当前代码中找到。
- 组件职责以 `docs/design.md` 和 `docs/plan.md` 为准；评审时使用 `docs/review-checklist.md`。
