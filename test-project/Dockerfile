# 多阶段构建 - 构建阶段
FROM maven:3.8.6-openjdk-11-slim AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖（利用 Docker 层缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests -B

# 运行阶段 - 使用更小的基础镜像
FROM openjdk:11.0.16-slim

# 安装必要的包并创建非 root 用户
RUN apt-get update && apt-get install -y --no-install-recommends wget curl && \
    groupadd -r appuser && useradd -r -g appuser appuser && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 更改文件所有者为非 root 用户
RUN chown -R appuser:appuser /app

# 切换到非 root 用户
USER appuser

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# 设置 JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]