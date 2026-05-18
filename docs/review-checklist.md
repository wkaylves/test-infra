# test-common 评审清单

本清单用于评审 `test-common` 的文档、模块边界、测试基础设施和代码质量。核心原则：**以代码为准，组件能力归属组件模块，文档示例必须可运行**。

## 1. 模块职责

- [ ] 新能力是否放在所属组件模块，而不是塞入 `test-common-junit5` 或 `test-common-spock`
- [ ] `test-common-junit5` 是否只包含纯 JUnit5 能力，不引入 Spring MVC、MyBatis、ES、MQ、RPC 等组件依赖
- [ ] `test-common-spock` 是否只包含纯 Spock 能力，不引入组件级 Spring/Testcontainers 配置
- [ ] `test-common-all` 是否只做依赖聚合，不新增基类、工具类或配置
- [ ] `test-common-example` 是否只作为样例，不被业务项目直接依赖
- [ ] 新增组件是否在 `docs/design.md` 的职责表中补充边界

## 2. 文档真实性

- [ ] README 中出现的类名、注解、模块名是否都能在当前代码中找到
- [ ] README 是否按测试场景推荐组件，而不是先推荐 JUnit5/Spock 大入口
- [ ] 文档是否避免使用尚未落地的目标态 API
- [ ] 示例依赖坐标是否与 Gradle 模块名一致
- [ ] 每个推荐测试路径是否有对应测试或 example
- [ ] 历史评审文档是否没有被误用为当前 API 说明

## 3. 测试路径

- [ ] Service 单测是否默认不启动 Spring 容器
- [ ] 纯 Mockito 场景是否优先使用原生 JUnit5 + Mockito，而不是无价值包装
- [ ] Controller 测试是否归属 `test-common-spring-mvc`
- [ ] Mapper/DAO 测试是否归属 `test-common-orm:test-common-mybatis` 或 `test-common-orm:test-common-jpa`
- [ ] HTTP 依赖隔离是否归属 `test-common-http-mock`
- [ ] MQ/RPC/调度/存储能力是否归属对应组件模块
- [ ] JUnit5 与 Spock 双入口是否放在同一个组件模块中维护

## 4. 依赖边界

- [ ] `api` / `implementation` / `compileOnly` / `testImplementation` 是否符合消费者需要
- [ ] 基础模块是否避免引入重型组件依赖
- [ ] 组件模块是否只暴露使用者真正需要的依赖
- [ ] 版本号是否集中在根 `build.gradle` 的 `ext` 中
- [ ] 是否避免把 Spring Boot starter 扩散到无 Spring 职责的模块
- [ ] `test-common-all` 是否没有制造新的传递依赖语义

## 5. 共享状态与并发

- [ ] 静态缓存是否有清理机制
- [ ] 静态 server/container 是否不会在并行测试中串扰
- [ ] 按方法名缓存 stub 是否能处理重载方法或同名方法
- [ ] Testcontainers 单例是否不会因首次调用锁死错误配置
- [ ] System properties 是否会污染其他测试
- [ ] 生命周期方法是否支持重复 start/stop 或清晰声明不支持

## 6. 容器与外部依赖

- [ ] 容器默认镜像、库名、用户名、密码是否可配置
- [ ] Spring 测试是否优先使用 `DynamicPropertySource` 注入动态端口
- [ ] 容器 stop 策略是否明确，是复用、延迟停止还是交给 JVM 退出
- [ ] 外部依赖 mock 是否支持常见状态码、请求体、响应体、header
- [ ] HTTP mock 是否区分 JSON、XML、text、form 等内容类型
- [ ] MQ/RPC mock 是否只模拟基础交互，不隐含业务语义

## 7. 类型安全与输入校验

- [ ] public API 是否校验 null、empty、非法数字
- [ ] 泛型是否避免 raw type
- [ ] builder 的集合返回是否避免被外部意外修改
- [ ] JSON path 读取是否清晰处理缺失路径和类型转换
- [ ] 反射解析注解时是否保留可诊断错误，避免静默失败造成误判

## 8. 测试覆盖

- [ ] 每个核心工具类是否有正向和异常路径测试
- [ ] 每个推荐测试路径是否有最小可运行样例
- [ ] JUnit5 与 Spock 入口是否分别有覆盖
- [ ] 容器类是否至少有不依赖 Docker 的结构测试，Docker 路径单独标记
- [ ] 文档示例是否能被现有测试或 example 间接验证

## 9. 构建验证

- [ ] `./gradlew testClasses --continue` 是否通过
- [ ] `./gradlew test --continue` 是否通过
- [ ] 是否记录 Gradle deprecation warning 中需要后续处理的问题
- [ ] 新增模块是否在 `settings.gradle` 中声明
- [ ] 新增文档是否不引用不存在的文件或类

## 10. 代码维护性

- [ ] 基类是否提供真实团队约定，而不是只包一层框架注解
- [ ] 工具类是否保持小而明确，不承载业务逻辑
- [ ] 注释是否解释约束和边界，而不是重复代码
- [ ] 不同组件是否避免复制粘贴式 builder，可抽象时是否已有真实重复
- [ ] 命名是否直接表达测试场景，例如 `BaseH2MapperTest`、`BaseWireMockSpec`
