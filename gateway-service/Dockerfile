# 멀티스테이지 빌드 - 빌드 스테이지
FROM gradle:8.5-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 래퍼와 빌드 파일들 복사
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle* .

# 소스 코드 복사
COPY src/ src/

# 실행 권한 부여 및 애플리케이션 빌드
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-daemon

# 런타임 스테이지
FROM eclipse-temurin:17-jre

# 애플리케이션 실행을 위한 사용자 생성
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출 (Gateway 서비스 포트)
EXPOSE 8080

# JVM 옵션 설정 (메모리 최적화)
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# 애플리케이션 시작
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
