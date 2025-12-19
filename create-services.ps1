# Script tạo tất cả microservices cho CollabSphere - Windows PowerShell

$SERVICES = @(
    "auth-service",
    "subject-service",
    "class-service",
    "project-service",
    "team-service",
    "workspace-service",
    "communication-service",
    "collaboration-service",
    "resource-service",
    "evaluation-service",
    "notification-service",
    "ai-service"
)

# Tạo thư mục services nếu chưa có
if (-not (Test-Path "services")) {
    New-Item -ItemType Directory -Path "services" | Out-Null
}

Write-Host "🚀 Đang tạo các services cho CollabSphere..." -ForegroundColor Green

$portCounter = 0

# Lặp qua từng service
foreach ($service in $SERVICES) {
    Write-Host "📦 Tạo $service..." -ForegroundColor Yellow
    
    # Tạo cấu trúc thư mục
    $serviceName = $service -replace "-service", ""
    $basePath = "services\$service"
    
    # Tạo folders
    New-Item -ItemType Directory -Path "$basePath\src\main\java\com\collab\$serviceName`service" -Force | Out-Null
    New-Item -ItemType Directory -Path "$basePath\src\main\resources" -Force | Out-Null
    New-Item -ItemType Directory -Path "$basePath\src\test\java\com\collab\$serviceName`service" -Force | Out-Null
    
    # Tạo pom.xml
    $pomContent = @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.collab</groupId>
        <artifactId>collab-sphere</artifactId>
        <version>1.0.0</version>
        <relativePath>../../pom.xml</relativePath>
    </parent>

    <artifactId>$service</artifactId>
    <n>$service</n>

    <dependencies>
        <!-- Shared Library -->
        <dependency>
            <groupId>com.collab</groupId>
            <artifactId>shared</artifactId>
            <version>1.0.0</version>
        </dependency>

        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- RabbitMQ -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
"@

    Set-Content -Path "$basePath\pom.xml" -Value $pomContent -Encoding UTF8

    # Tạo application.yml
    $servicePort = 8081 + $portCounter
    $ymlContent = @"
spring:
  application:
    name: $service
  profiles:
    active: dev
  
  # Cấu hình JPA/Hibernate
  datasource:
    url: jdbc:postgresql://`${SPRING_DATASOURCE_URL:localhost:5432/collab_${serviceName}_db}
    username: `${SPRING_DATASOURCE_USERNAME:postgres}
    password: `${SPRING_DATASOURCE_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update  # Dev: update | Prod: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  # Cấu hình RabbitMQ
  rabbitmq:
    host: `${RABBITMQ_HOST:localhost}
    port: `${RABBITMQ_PORT:5672}
    username: `${RABBITMQ_USERNAME:guest}
    password: `${RABBITMQ_PASSWORD:guest}
    virtual-host: /
    connection-timeout: 10000ms

server:
  port: $servicePort
  servlet:
    context-path: /api/v1

logging:
  level:
    root: INFO
    com.collab: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n"
"@

    Set-Content -Path "$basePath\src\main\resources\application.yml" -Value $ymlContent -Encoding UTF8

    # Tạo Dockerfile
    $dockerfileContent = @"
FROM maven:3.9.0-eclipse-temurin-17 as builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-slim

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
"@

    Set-Content -Path "$basePath\Dockerfile" -Value $dockerfileContent -Encoding UTF8

    # Tạo README.md
    $readmeContent = @"
# $service

Mô tả về service này.

## Chạy service

``````bash
mvn spring-boot:run
``````

## Build Docker image

``````bash
docker build -t collab-sphere/$service:1.0.0 .
``````
"@

    Set-Content -Path "$basePath\README.md" -Value $readmeContent -Encoding UTF8

    # Tạo Main Application class
    $serviceClassName = ($service -replace "-", " " | Get-Culture).TextInfo.ToTitleCase() -replace " ", ""
    $serviceClassName = $serviceClassName -replace "Service", "ServiceApplication"
    
    $javaContent = @"
package com.collab.${serviceName}service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point cho $service
 */
@SpringBootApplication
public class ${serviceClassName} {

    public static void main(String[] args) {
        SpringApplication.run(${serviceClassName}.class, args);
    }
}
"@

    Set-Content -Path "$basePath\src\main\java\com\collab\$serviceName`service\$serviceClassName.java" -Value $javaContent -Encoding UTF8

    Write-Host "✅ Tạo $service thành công" -ForegroundColor Green
    $portCounter++
}

Write-Host ""
Write-Host "🎉 Tất cả services đã được tạo!" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Các bước tiếp theo:" -ForegroundColor Cyan
Write-Host "1. Cập nhật parent pom.xml để thêm modules (xem hướng dẫn bên dưới)"
Write-Host "2. Build toàn bộ dự án: mvn clean package"
Write-Host "3. Chạy docker-compose: docker-compose up --build"