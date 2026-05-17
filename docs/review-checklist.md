# 一次性评审完整清单

基于七轮评审（251 个问题）反推的系统性审查清单。逐文件逐项打勾，全部通过后再提交。

## 环境

- **Java**: `/Users/kaylves/Library/Java/JavaVirtualMachines/temurin-18.0.2.1/Contents/Home`
- **Maven**: `/Users/kaylves/java/apache-maven-3.5.3-bin/apache-maven-3.5.3`
- **Maven 启动**:
  ```bash
  export MAVEN_HOME=/Users/kaylves/java/apache-maven-3.5.3-bin/apache-maven-3.5.3 && export PATH="$MAVEN_HOME/bin:$PATH"
  ```

---

## 1. 构建与依赖
- [ ] pom.xml 依赖版本是否统一（dependencyManagement）
- [ ] 有无废弃依赖（mysql:mysql-connector-java → com.mysql:mysql-connector-j）
- [ ] 有无未使用的依赖
- [ ] 有无硬编码版本号（应由 BOM 管理）
- [ ] 插件版本是否集中管理（pluginManagement）
- [ ] 测试依赖是否 scope=test
- [ ] spring-boot-maven-plugin 是否多余（非可执行模块）
- [ ] maven-failsafe-plugin 是否配置（integration 测试）

## 2. 资源管理（JDBC/IO/Process）
- [ ] 所有 Connection/Statement/ResultSet 是否 try-with-resources
- [ ] Process 是否有超时 + destroyForcibly
- [ ] InterruptedException 是否恢复中断状态（Thread.currentThread().interrupt()）
- [ ] 异常链是否保留（addSuppressed）
- [ ] DCL 单例是否先 start() 后赋值（防半初始化泄漏）

## 3. 类型安全
- [ ] 泛型方法是否遮蔽类级泛型
- [ ] 返回值是否 raw type（应加泛型）
- [ ] instanceof 检查是否在 ClassCastException 风险点前做
- [ ] 数字比较是否处理跨类型（Integer vs Long → BigDecimal）
- [ ] BigDecimal 转换是否处理 Infinity/NaN
- [ ] JSON 反序列化是否处理 Map → Object 路径

## 4. 输入校验
- [ ] 所有 public 方法的 String 参数是否 null/empty 检查
- [ ] 数值参数是否 null/负数/零 检查
- [ ] 校验失败是否抛 IllegalArgumentException（不是静默返回错误结果）

## 5. 并发与事务
- [ ] @Transactional 方法内是否有 MQ 发送（回滚后消息已发）
- [ ] 订单号等唯一标识是否并发安全（UUID，非自增）
- [ ] 缓存操作是否与 DB 操作一致（缓存回填、删除）

## 6. 可见性与封装
- [ ] 子类需要的方法是否 protected（不是 package-private）
- [ ] 不应暴露的方法是否 private（如容器 start()）
- [ ] Builder 的 varargs 是否返回 unmodifiableList

## 7. 测试覆盖
- [ ] 每个 Service 方法是否有正向测试
- [ ] 每个 Service 方法是否有负向测试（null/empty/非法状态）
- [ ] 缓存命中/未命中/DB-null 三种路径是否都测到
- [ ] Map 反序列化路径是否测到
- [ ] 边界值是否测到（空列表、零值、负数）

## 8. 测试基础设施
- [ ] Mock 方法参数是否与实际调用一致（eq() vs any()）
- [ ] Spock stub 是否在 given: 块（不是 then: 块）
- [ ] @Mock 是否在 Mockito 生命周期内（@ExtendWith）
- [ ] 测试资源文件是否在 src/test/resources（不是 src/main）

## 9. 异常处理
- [ ] catch 块是否吞异常（至少 log 或 addSuppressed）
- [ ] finally 块是否清理资源
- [ ] 容器 stop() 失败是否影响原始异常

## 10. 序列化与兼容
- [ ] 缓存对象是否实现 Serializable
- [ ] RocketMQ topic 名是否合法（无冒号等特殊字符）
- [ ] SQL 表名是否有正则校验（防注入）
