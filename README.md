# Maven Release by File Action

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub Release](https://img.shields.io/github/release/rosestack/maven-release-by-file-action.svg)](https://github.com/rosestack/maven-release-by-file-action/releases)

A comprehensive GitHub Actions workflow for automating Maven project releases with version management driven by `.github/project.yml` file. This action streamlines the entire release process from branch creation to Maven Central deployment and documentation publishing.

[English](README.md) | [中文文档](README.zh-CN.md)

## 🚀 Features

* 📄 **File-Based Version Management** - Define release and next versions in `.github/project.yml`
* 🔄 **Automated Release Workflow** - Complete release process with branch management
* 📦 **Maven Central Deployment** - Deploy artifacts with GPG signing to Maven Central (Sonatype OSSRH)
* 📚 **Documentation Publishing** - Deploy Maven site to GitHub Pages
* 🏷️ **Git Tag Management** - Automatic tag creation and GitHub releases
* 🎯 **Milestone Management** - Close current milestone and create next version milestone
* 🧪 **Testing Support** - Optional test execution with configurable skip
* 🔐 **Secure Signing** - GPG signing of all artifacts
* 📁 **Multi-module Support** - Works with submodules via working directory configuration
* ⚙️ **Highly Configurable** - Customize Java version, Maven args, profiles, and more

## 🔄 Release Process

The action automates the following workflow:

1. **Edit `.github/project.yml`** - Update release and next versions
2. **Create PR and merge** - Review and merge version changes
3. **Trigger Release** - Action automatically executes upon merge
4. **Create Release Branch** - Creates a branch named after release version
5. **Update Version** - Sets release version in POM files
6. **Build & Deploy** - Builds, tests, signs, and deploys to Maven Central
7. **Create Tag** - Creates git tag and GitHub release
8. **Deploy Docs** - Publishes Maven site to GitHub Pages (optional)
9. **Update Main Branch** - Sets next development version and merges back
10. **Manage Milestone** - Closes current milestone, creates next one

## 📋 Prerequisites

* Maven project configured for Maven Central deployment
* GPG key for signing artifacts
* Maven Central account and credentials
* GitHub Actions enabled on your repository
* GitHub token with appropriate permissions
* `.github/project.yml` file with version information

## 🔧 Setup

### 1. Create Project Metadata File

Create `.github/project.yml` in your repository:

```yaml
release:
  current-version: "0.0.1"
  next-version: "0.0.2-SNAPSHOT"
```

### 2. Configure Secrets

Add the following secrets to your GitHub repository (Settings → Secrets and variables → Actions):

#### GPG Key Setup

```bash
# Generate GPG key (if you don't have one)
gpg --full-generate-key

# List your keys
gpg --list-secret-keys --keyid-format=long

# Export private key (replace YOUR_KEY_ID with your actual key ID)
gpg --armor --export-secret-keys YOUR_KEY_ID > private-key.asc

# Copy the content of private-key.asc to GitHub Secrets
cat private-key.asc

# Upload public key to key servers
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

Add to GitHub Secrets:
* `GPG_PRIVATE_KEY`: Content of `private-key.asc`
* `GPG_PASSPHRASE`: Your GPG key passphrase

#### Maven Central Setup

1. Create account at https://central.sonatype.com
2. Request namespace verification (e.g., `io.github.yourusername`)
3. Generate User Token from your account

Add to GitHub Secrets:
* `MAVEN_USERNAME`: Your OSSRH username or token username
* `MAVEN_PASSWORD`: Your OSSRH password or token password

#### GitHub Token

The default `${{ secrets.GITHUB_TOKEN }}` is usually sufficient. Ensure your workflow has these permissions:

```yaml
permissions:
  contents: write      # For creating releases and pushing commits
  pages: write         # For GitHub Pages deployment
  id-token: write      # For GitHub Pages deployment
```

### 3. Create Release Workflow

Create `.github/workflows/release.yml`:

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

## 📝 Inputs

### Required Inputs

| Input            | Description                              | Example              |
|------------------|------------------------------------------|----------------------|
| `maven-username` | Maven Central username or token          | From secrets         |
| `maven-password` | Maven Central password or token          | From secrets         |
| `gpg-private-key`| GPG private key for signing              | From secrets         |
| `gpg-passphrase` | GPG key passphrase                       | From secrets         |
| `github-token`   | GitHub token with write permissions      | `${{ secrets.GITHUB_TOKEN }}` |

### Optional Inputs

| Input                | Description                              | Default              |
|----------------------|------------------------------------------|----------------------|
| `java-version`       | Java version to use                      | `8`                  |
| `java-distribution`  | Java distribution (temurin, zulu, etc.)  | `temurin`            |
| `maven-args`         | Additional Maven arguments               | `-B -U -ntp`         |
| `maven-profiles`     | Maven profiles to activate               | `central`            |
| `maven-server-id`    | Maven server ID for authentication       | `central`            |
| `skip-tests`         | Skip running tests                       | `false`              |
| `deploy-pages`       | Deploy Maven site to GitHub Pages        | `true`               |
| `working-directory`  | Working directory for Maven commands     | `.`                  |
| `metadata-file-path` | Path to project metadata file            | `.github/project.yml`|

## 💡 Usage Examples

### Basic Usage

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

### Advanced Configuration

```yaml
- name: Release Maven Project
  uses: rosestack/maven-release-by-file-action@main
  with:
    # Version Management
    metadata-file-path: '.github/project.yml'
    
    # Java Configuration
    java-version: '17'
    java-distribution: 'temurin'
    
    # Maven Central Authentication
    maven-username: ${{ secrets.MAVEN_USERNAME }}
    maven-password: ${{ secrets.MAVEN_PASSWORD }}
    maven-server-id: 'central'
    
    # GPG Signing
    gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
    gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}
    
    # GitHub Configuration
    github-token: ${{ secrets.GITHUB_TOKEN }}
    
    # Build Options
    maven-args: '-B -U -ntp -T 1C'
    maven-profiles: 'central'
    skip-tests: 'false'
    deploy-pages: 'true'
    
    # Advanced Options
    working-directory: '.'
```

### Multi-module Project

For projects with submodules:

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

### Skip Tests for Hotfix

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

### Custom Java Version

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

## 📦 Maven POM Configuration

Your `pom.xml` must include required metadata and plugins for Maven Central deployment.

### Essential Configuration

```xml
<project>
  <!-- Project coordinates -->
  <groupId>io.github.yourusername</groupId>
  <artifactId>your-project</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>jar</packaging>
  
  <!-- Required metadata -->
  <name>Your Project</name>
  <description>Project description</description>
  <url>https://github.com/yourusername/your-project</url>
  
  <!-- License -->
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>https://www.apache.org/licenses/LICENSE-2.0</url>
    </license>
  </licenses>
  
  <!-- Developers -->
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
  
  <!-- Distribution Management -->
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
  
  <!-- Profiles -->
  <profiles>
    <profile>
      <id>central</id>
      <build>
        <plugins>
          <!-- GPG Plugin -->
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
          
          <!-- Source Plugin -->
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
          
          <!-- Javadoc Plugin -->
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

## 🎯 How to Release

### Step 1: Update Version Information

Edit `.github/project.yml`:

```yaml
release:
  current-version: "0.0.1"      # Your release version (without -SNAPSHOT)
  next-version: "0.0.2-SNAPSHOT"  # Next development version
```

### Step 2: Create Pull Request

```bash
git checkout -b release-0.0.1
git add .github/project.yml
git commit -m "Prepare release 0.0.1"
git push origin release-0.0.1
```

Create a PR and get it reviewed.

### Step 3: Merge and Release

Once the PR is merged to main, the action will automatically:
1. Create release branch `0.0.1`
2. Update POM versions to `0.0.1`
3. Build and deploy to Maven Central
4. Create tag `v0.0.1` and GitHub release
5. Deploy documentation to GitHub Pages
6. Update main branch to `0.0.2-SNAPSHOT`
7. Close milestone `0.0.1` and create `0.0.2-SNAPSHOT`

## 🔧 Troubleshooting

### Release Fails with GPG Error

**Problem**: GPG signing fails during deployment

**Solutions**:
* Ensure GPG private key includes `BEGIN` and `END` markers
* Verify passphrase is correct
* Check public key is uploaded to key servers:
  ```bash
  gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
  gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
  ```

### Maven Central Deployment Fails

**Problem**: Artifacts fail to deploy to Maven Central

**Solutions**:
* Verify OSSRH credentials are correct
* Ensure namespace is approved in Sonatype
* Check all required POM metadata is present
* Verify artifacts are properly signed
* Check `distributionManagement` configuration in POM

### Merge Conflicts

**Problem**: Automatic merge to main branch fails

**Solutions**:
* Ensure main branch is up to date before release
* Resolve any pending PRs that might conflict
* Check if anyone else is releasing at the same time

### Version Format Issues

**Problem**: Action rejects the version format

**Solutions**:
* Release version must NOT contain `-SNAPSHOT`
* Next version MUST contain `-SNAPSHOT`
* Follow semantic versioning: `MAJOR.MINOR.PATCH`

## 📊 Comparison with Similar Actions

### vs. maven-release-by-manual-action

| Feature | maven-release-by-file-action | maven-release-by-manual-action |
|---------|------------------------------|-------------------------------|
| Version Input | File-based (`.github/project.yml`) | Manual workflow_dispatch |
| Automation | Fully automated on merge | Manual trigger |
| Best For | CI/CD pipelines | On-demand releases |
| PR Review | Yes, review version changes | No PR involved |

### vs. maven-release-plugin

| Feature | maven-release-by-file-action | maven-release-plugin |
|---------|------------------------------|---------------------|
| Platform | GitHub Actions | Local Maven |
| Automation | Complete workflow | Requires manual steps |
| Git Management | Automated | Manual commands |
| Milestone Management | Built-in | Not included |

## 📄 License

Apache License 2.0 - see [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development Setup

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🙏 Acknowledgments

* Inspired by [maven-release-by-manual-action](https://github.com/rosestack/maven-release-by-manual-action)
* Uses [actions/checkout](https://github.com/actions/checkout)
* Uses [actions/setup-java](https://github.com/actions/setup-java)
* Uses [peaceiris/actions-gh-pages](https://github.com/peaceiris/actions-gh-pages)
* Uses [rosestack/milestone-release-action](https://github.com/rosestack/milestone-release-action)
* Uses [radcortez/project-metadata-action](https://github.com/radcortez/project-metadata-action)

## 📧 Support

For issues and questions:

* GitHub Issues: https://github.com/rosestack/maven-release-by-file-action/issues
* Email: ichensoul@gmail.com

## 🌟 Star History

If you find this project useful, please consider giving it a star ⭐️

---

Made with ❤️ by [rosestack](https://github.com/rosestack)
