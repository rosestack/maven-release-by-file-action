# Maven Release by File Action

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub Release](https://img.shields.io/github/release/rosestack/maven-release-by-file-action.svg)](https://github.com/rosestack/maven-release-by-file-action/releases)

一个功能完整的 GitHub Actions 工作流，用于自动化 Maven 项目发布，通过 `.github/project.yml` 文件管理版本。此 Action 简化了从分支创建到 Maven Central 部署和文档发布的整个发布流程。

[English](README.md) | [中文文档](README.zh-CN.md)

## 🚀 功能特性

* 📄 **基于文件的版本管理** - 在 `.github/project.yml` 中定义发布版本和下一个版本
* 🔄 **自动化发布工作流** - 包含分支管理的完整发布流程
* 📦 **Maven Central 部署** - 使用 GPG 签名将制品部署到 Maven Central (Sonatype OSSRH)
* 📚 **文档发布** - 将 Maven 站点部署到 GitHub Pages
* 🏷️ **Git 标签管理** - 自动创建标签和 GitHub 发布
* 🎯 **里程碑管理** - 关闭当前里程碑并创建下一个版本里程碑
* 🧪 **测试支持** - 可选的测试执行，支持可配置跳过
* 🔐 **安全签名** - 对所有制品进行 GPG 签名
* 📁 **多模块支持** - 通过工作目录配置支持子模块
* ⚙️ **高度可配置** - 自定义 Java 版本、Maven 参数、配置文件等

## 🔄 发布流程

此 Action 自动化以下工作流：

1. **编辑 `.github/project.yml`** - 更新发布版本和下一个版本
2. **创建 PR 并合并** - 审查和合并版本变更
3. **触发发布** - 合并后 Action 自动执行
4. **创建发布分支** - 创建以发布版本命名的分支
5. **更新版本** - 在 POM 文件中设置发布版本
6. **构建和部署** - 构建、测试、签名并部署到 Maven Central
7. **创建标签** - 创建 git 标签和 GitHub 发布
8. **部署文档** - 将 Maven 站点发布到 GitHub Pages（可选）
9. **更新主分支** - 设置下一个开发版本并合并回主分支
10. **管理里程碑** - 关闭当前里程碑，创建下一个里程碑

## 📋 前置要求

* 已配置 Maven Central 部署的 Maven 项目
* 用于签名制品的 GPG 密钥
* Maven Central (OSSRH) 账户和凭据
* 启用了 GitHub Actions 的仓库
* 具有适当权限的 GitHub token
* 包含版本信息的 `.github/project.yml` 文件

## 🔧 设置

### 1. 创建项目元数据文件

在仓库中创建 `.github/project.yml`：

```yaml
release:
  current-version: "1.0.0"
  next-version: "1.0.1-SNAPSHOT"
```

### 2. 配置密钥

将以下密钥添加到您的 GitHub 仓库（Settings → Secrets and variables → Actions）：

#### GPG 密钥设置

```bash
# 生成 GPG 密钥（如果还没有）
gpg --full-generate-key

# 列出您的密钥
gpg --list-secret-keys --keyid-format=long

# 导出私钥（将 YOUR_KEY_ID 替换为您的实际密钥 ID）
gpg --armor --export-secret-keys YOUR_KEY_ID > private-key.asc

# 将 private-key.asc 的内容复制到 GitHub Secrets
cat private-key.asc

# 将公钥上传到密钥服务器
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

添加到 GitHub Secrets：
* `GPG_PRIVATE_KEY`: `private-key.asc` 的内容
* `GPG_PASSPHRASE`: 您的 GPG 密钥密码

#### Maven Central 设置

1. 在 https://central.sonatype.com 创建账户
2. 请求命名空间验证（例如：`io.github.yourusername`）
3. 从您的账户生成用户令牌

添加到 GitHub Secrets：
* `MAVEN_USERNAME`: 您的 OSSRH 用户名或令牌用户名
* `MAVEN_PASSWORD`: 您的 OSSRH 密码或令牌密码

#### GitHub Token

默认的 `${{ secrets.GITHUB_TOKEN }}` 通常就足够了。确保您的工作流具有以下权限：

```yaml
permissions:
  contents: write      # 用于创建发布和推送提交
  pages: write         # 用于 GitHub Pages 部署
  id-token: write      # 用于 GitHub Pages 部署
```

### 3. 创建发布工作流

创建 `.github/workflows/release.yml`：

```yaml
name: Maven Release

on:
  push:
    paths:
      - '.github/project.yml'
    branches:
      - main

permissions:
  contents: write
  pages: write
  id-token: write

jobs:
  release:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4
        with:
          fetch-depth: 0
          token: ${{ secrets.GITHUB_TOKEN }}
      
      - name: Release Maven Project
        uses: rosestack/maven-release-by-file-action@main
        with:
          maven-username: ${{ secrets.MAVEN_USERNAME }}
          maven-password: ${{ secrets.MAVEN_PASSWORD }}
          gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
          gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}
          github-token: ${{ secrets.GITHUB_TOKEN }}
```

## 📝 输入参数

### 必需输入

| 输入参数             | 描述                            | 示例                      |
|---------------------|--------------------------------|--------------------------|
| `maven-username`    | Maven Central 用户名或令牌       | 来自 secrets             |
| `maven-password`    | Maven Central 密码或令牌         | 来自 secrets             |
| `gpg-private-key`   | 用于签名的 GPG 私钥              | 来自 secrets             |
| `gpg-passphrase`    | GPG 密钥密码                     | 来自 secrets             |
| `github-token`      | 具有写权限的 GitHub token        | `${{ secrets.GITHUB_TOKEN }}` |

### 可选输入

| 输入参数                | 描述                              | 默认值                   |
|-----------------------|----------------------------------|-------------------------|
| `java-version`        | 要使用的 Java 版本                | `8`                     |
| `java-distribution`   | Java 发行版 (temurin, zulu 等)    | `temurin`               |
| `maven-args`          | 额外的 Maven 参数                 | `-B -U -ntp`            |
| `maven-profiles`      | 要激活的 Maven 配置文件            | `central`               |
| `maven-server-id`     | 用于身份验证的 Maven 服务器 ID     | `central`               |
| `skip-tests`          | 跳过运行测试                      | `false`                 |
| `deploy-pages`        | 将 Maven 站点部署到 GitHub Pages  | `true`                  |
| `working-directory`   | Maven 命令的工作目录               | `.`                     |
| `metadata-file-path`  | 项目元数据文件路径                 | `.github/project.yml`   |
| `main-branch`         | 主分支名称                        | `main`                  |

## 💡 使用示例

### 基本用法

```yaml
- name: Release Maven Project
  uses: rosestack/maven-release-by-file-action@main
  with:
    maven-username: ${{ secrets.MAVEN_USERNAME }}
    maven-password: ${{ secrets.MAVEN_PASSWORD }}
    gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
    gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}
    github-token: ${{ secrets.GITHUB_TOKEN }}
```

### 高级配置

```yaml
- name: Release Maven Project
  uses: rosestack/maven-release-by-file-action@main
  with:
    # 版本管理
    metadata-file-path: '.github/project.yml'
    main-branch: 'main'
    
    # Java 配置
    java-version: '17'
    java-distribution: 'temurin'
    
    # Maven Central 身份验证
    maven-username: ${{ secrets.MAVEN_USERNAME }}
    maven-password: ${{ secrets.MAVEN_PASSWORD }}
    maven-server-id: 'central'
    
    # GPG 签名
    gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
    gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}
    
    # GitHub 配置
    github-token: ${{ secrets.GITHUB_TOKEN }}
    
    # 构建选项
    maven-args: '-B -U -ntp -T 1C'
    maven-profiles: 'central'
    skip-tests: 'false'
    deploy-pages: 'true'
    
    # 高级选项
    working-directory: '.'
```

### 多模块项目

对于有子模块的项目：

```yaml
- name: Release Maven Project
  uses: rosestack/maven-release-by-file-action@main
  with:
    working-directory: 'my-module'
    maven-username: ${{ secrets.MAVEN_USERNAME }}
    maven-password: ${{ secrets.MAVEN_PASSWORD }}
    gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
    gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}
    github-token: ${{ secrets.GITHUB_TOKEN }}
```

### 热修复跳过测试

```yaml
- name: Release Maven Project
  uses: rosestack/maven-release-by-file-action@main
  with:
    skip-tests: 'true'
    maven-username: ${{ secrets.MAVEN_USERNAME }}
    maven-password: ${{ secrets.MAVEN_PASSWORD }}
    gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
    gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}
    github-token: ${{ secrets.GITHUB_TOKEN }}
```

### 自定义 Java 版本

```yaml
- name: Release Maven Project
  uses: rosestack/maven-release-by-file-action@main
  with:
    java-version: '21'
    java-distribution: 'temurin'
    maven-username: ${{ secrets.MAVEN_USERNAME }}
    maven-password: ${{ secrets.MAVEN_PASSWORD }}
    gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
    gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}
    github-token: ${{ secrets.GITHUB_TOKEN }}
```

## 📦 Maven POM 配置

您的 `pom.xml` 必须包含 Maven Central 部署所需的元数据和插件。

### 基本配置

```xml
<project>
  <!-- 项目坐标 -->
  <groupId>io.github.yourusername</groupId>
  <artifactId>your-project</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>
  
  <!-- 必需的元数据 -->
  <name>Your Project</name>
  <description>项目描述</description>
  <url>https://github.com/yourusername/your-project</url>
  
  <!-- 许可证 -->
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>https://www.apache.org/licenses/LICENSE-2.0</url>
    </license>
  </licenses>
  
  <!-- 开发者 -->
  <developers>
    <developer>
      <name>Your Name</name>
      <email>your.email@example.com</email>
      <organization>Your Organization</organization>
      <organizationUrl>https://github.com/yourusername</organizationUrl>
    </developer>
  </developers>
  
  <!-- SCM -->
  <scm>
    <connection>scm:git:git://github.com/yourusername/your-project.git</connection>
    <developerConnection>scm:git:ssh://github.com:yourusername/your-project.git</developerConnection>
    <url>https://github.com/yourusername/your-project</url>
  </scm>
  
  <!-- 分发管理 -->
  <distributionManagement>
    <repository>
      <id>central</id>
      <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
    </repository>
    <snapshotRepository>
      <id>central</id>
      <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
    </snapshotRepository>
  </distributionManagement>
  
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.11.0</version>
      </plugin>
    </plugins>
  </build>
  
  <!-- 配置文件 -->
  <profiles>
    <profile>
      <id>central</id>
      <build>
        <plugins>
          <!-- GPG 插件 -->
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-gpg-plugin</artifactId>
            <version>3.1.0</version>
            <executions>
              <execution>
                <id>sign-artifacts</id>
                <phase>verify</phase>
                <goals>
                  <goal>sign</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
          
          <!-- 源码插件 -->
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-source-plugin</artifactId>
            <version>3.3.0</version>
            <executions>
              <execution>
                <id>attach-sources</id>
                <goals>
                  <goal>jar-no-fork</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
          
          <!-- Javadoc 插件 -->
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>3.6.0</version>
            <executions>
              <execution>
                <id>attach-javadocs</id>
                <goals>
                  <goal>jar</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
    </profile>
  </profiles>
</project>
```

## 🎯 如何发布

### 步骤 1：更新版本信息

编辑 `.github/project.yml`：

```yaml
release:
  current-version: "1.0.0"      # 您的发布版本（不带 -SNAPSHOT）
  next-version: "1.0.1-SNAPSHOT"  # 下一个开发版本
```

### 步骤 2：创建 Pull Request

```bash
git checkout -b release-1.0.0
git add .github/project.yml
git commit -m "Prepare release 1.0.0"
git push origin release-1.0.0
```

创建 PR 并进行审查。

### 步骤 3：合并并发布

一旦 PR 合并到 main，Action 将自动：
1. 创建发布分支 `1.0.0`
2. 将 POM 版本更新为 `1.0.0`
3. 构建并部署到 Maven Central
4. 创建标签 `v1.0.0` 和 GitHub 发布
5. 将文档部署到 GitHub Pages
6. 将主分支更新为 `1.0.1-SNAPSHOT`
7. 关闭里程碑 `1.0.0` 并创建 `1.0.1-SNAPSHOT`

## 🔧 故障排查

### GPG 错误导致发布失败

**问题**：部署期间 GPG 签名失败

**解决方案**：
* 确保 GPG 私钥包含 `BEGIN` 和 `END` 标记
* 验证密码正确
* 检查公钥是否已上传到密钥服务器：
  ```bash
  gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
  gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
  ```

### Maven Central 部署失败

**问题**：制品无法部署到 Maven Central

**解决方案**：
* 验证 OSSRH 凭据正确
* 确保在 Sonatype 中已批准命名空间
* 检查所有必需的 POM 元数据是否存在
* 验证制品是否正确签名
* 检查 POM 中的 `distributionManagement` 配置

### 合并冲突

**问题**：自动合并到主分支失败

**解决方案**：
* 确保主分支在发布前是最新的
* 解决可能冲突的任何待处理 PR
* 检查是否有其他人同时在发布

### 版本格式问题

**问题**：Action 拒绝版本格式

**解决方案**：
* 发布版本不能包含 `-SNAPSHOT`
* 下一个版本必须包含 `-SNAPSHOT`
* 遵循语义化版本：`MAJOR.MINOR.PATCH`

## 📊 与类似 Actions 的比较

### vs. maven-release-by-manual-action

| 功能 | maven-release-by-file-action | maven-release-by-manual-action |
|------|------------------------------|-------------------------------|
| 版本输入 | 基于文件 (`.github/project.yml`) | 手动 workflow_dispatch |
| 自动化 | 合并时完全自动化 | 手动触发 |
| 适用于 | CI/CD 流水线 | 按需发布 |
| PR 审查 | 是，审查版本变更 | 不涉及 PR |

### vs. maven-release-plugin

| 功能 | maven-release-by-file-action | maven-release-plugin |
|------|------------------------------|---------------------|
| 平台 | GitHub Actions | 本地 Maven |
| 自动化 | 完整工作流 | 需要手动步骤 |
| Git 管理 | 自动化 | 手动命令 |
| 里程碑管理 | 内置 | 不包含 |

## 📄 许可证

Apache License 2.0 - 详见 [LICENSE](LICENSE) 文件。

## 🤝 贡献

欢迎贡献！请随时提交 Pull Request。对于重大更改，请先开一个 issue 讨论您想要更改的内容。

### 开发设置

1. Fork 仓库
2. 创建您的功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开一个 Pull Request

## 🙏 致谢

* 灵感来自 [maven-release-by-manual-action](https://github.com/rosestack/maven-release-by-manual-action)
* 使用 [actions/checkout](https://github.com/actions/checkout)
* 使用 [actions/setup-java](https://github.com/actions/setup-java)
* 使用 [peaceiris/actions-gh-pages](https://github.com/peaceiris/actions-gh-pages)
* 使用 [rosestack/milestone-release-action](https://github.com/rosestack/milestone-release-action)
* 使用 [radcortez/project-metadata-action](https://github.com/radcortez/project-metadata-action)

## 📧 支持

如有问题和疑问：

* GitHub Issues: https://github.com/rosestack/maven-release-by-file-action/issues
* Email: ichensoul@gmail.com

## 🌟 Star 历史

如果您觉得这个项目有用，请考虑给它一个 star ⭐️

---

由 [chensoul](https://github.com/rosestack) 用 ❤️ 制作

