# Maven Central 发布指南

本项目已配置 Maven Central 发布功能，使用 Sonatype OSSRH 进行发布。

## 前置准备

### 1. 注册 Sonatype 账号

1. 访问 https://issues.sonatype.org/ 注册账号
2. 创建 JIRA ticket 申请发布权限（选择 "New Project"）
3. 等待 Sonatype 审核通过（通常 1-2 个工作日）

### 2. 生成 GPG 密钥

```bash
# 生成 GPG 密钥
gpg --full-generate-key

# 查看密钥 ID
gpg --list-keys

# 导出私钥（base64 编码）
gpg --export-secret-keys YOUR_KEY_ID | base64

# 上传公钥到密钥服务器
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 3. 配置凭据

复制 `gradle.properties.template` 为 `gradle.properties`：

```bash
cp gradle.properties.template gradle.properties
```

编辑 `gradle.properties`，填入您的凭据：

```properties
# Sonatype OSSRH credentials
ossrhUsername=your_sonatype_username
ossrhPassword=your_sonatype_password

# GPG/PGP signing configuration
signing.key=your_gpg_private_key_base64
signing.password=your_gpg_passphrase
```

**注意：** `gradle.properties` 已添加到 `.gitignore`，不会被提交到版本控制。

## 发布流程

### 发布 SNAPSHOT 版本

```bash
# 修改版本号为 SNAPSHOT（在 build.gradle 中）
# version = '1.0.0-SNAPSHOT'

# 发布 SNAPSHOT 到 Maven Central
./gradlew publishToSonatype
```

### 发布正式版本

1. **修改版本号**：将 `build.gradle` 中的版本号改为正式版本（如 `1.0.0`）

2. **发布到 Staging**：
   ```bash
   ./gradlew publishToSonatype
   ```

3. **关闭并发布 Staging 仓库**：
   ```bash
   ./gradlew closeAndReleaseSonatypeStagingRepository
   ```

4. **等待同步**：发布后通常需要 10-30 分钟同步到 Maven Central

### 使用 GitHub Actions 自动发布

创建 `.github/workflows/release.yml`：

```yaml
name: Release to Maven Central

on:
  release:
    types: [created]

jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 8
        uses: actions/setup-java@v4
        with:
          java-version: '8'
          distribution: 'temurin'

      - name: Publish to Maven Central
        run: ./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
        env:
          OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
          OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
          GPG_SIGNING_KEY: ${{ secrets.GPG_SIGNING_KEY }}
          GPG_SIGNING_PASSWORD: ${{ secrets.GPG_SIGNING_PASSWORD }}
```

## 可用的 Gradle 任务

| 任务 | 说明 |
|------|------|
| `publishToSonatype` | 发布到 Sonatype OSSRH（Maven Central） |
| `publishMavenPublicationToMavenLocal` | 发布到本地 Maven 仓库 |
| `publishMavenPublicationToGitHubPackagesRepository` | 发布到 GitHub Packages |
| `closeAndReleaseSonatypeStagingRepository` | 关闭并发布 Staging 仓库 |
| `closeSonatypeStagingRepository` | 关闭 Staging 仓库 |
| `releaseSonatypeStagingRepository` | 发布已关闭的 Staging 仓库 |
| `publishToMavenCentral` | 发布到 Maven Central（OSSRH） |
| `releaseToMavenCentral` | 发布 Staging 仓库到 Maven Central |

## 验证发布

1. **检查 Staging 仓库**：访问 https://s01.oss.sonatype.org/
2. **检查 Maven Central**：访问 https://search.maven.org/
3. **使用依赖**：
   ```groovy
   implementation 'com.github.kaylves:test-infra-core:1.0.0'
   ```

## 常见问题

### Q: 发布失败，提示 "401 Unauthorized"
A: 检查 `ossrhUsername` 和 `ossrhPassword` 是否正确。

### Q: 发布失败，提示 "GPG signature verification failed"
A: 确保 GPG 密钥已上传到密钥服务器：
```bash
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### Q: 版本号已存在
A: Maven Central 不允许重复版本号，需要递增版本号。

### Q: 如何查看发布状态
A: 访问 https://s01.oss.sonatype.org/ 查看 Staging 仓库状态。

## 项目元数据

发布配置中包含以下元数据：

- **Group ID**: `com.github.kaylves`
- **License**: Apache License 2.0
- **SCM**: https://github.com/kaylves/test-infra
- **Developer**: kaylves

如需修改这些信息，请编辑 `gradle/publish-mavencentral.gradle` 文件。
