# 1단계: 빌드 스테이지
FROM openjdk:21-slim AS build

WORKDIR /app

# 의존성 파일 먼저 복사
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY gradlew gradlew
COPY gradle gradle

# 의존성 캐시 다운로드
RUN chmod +x ./gradlew && ./gradlew dependencies

# 애플리케이션 소스 복사
COPY src src

# 빌드 수행 (테스트 제외)
RUN ./gradlew build -x test

# 2단계: 실행 스테이지
FROM openjdk:21-jdk-slim

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# ✅ HTTPS 설정 파일 및 인증서 복사
COPY --from=build /app/src/main/resources/application.yml application.yml
COPY --from=build /app/src/main/resources/mycert.p12 mycert.p12

# HTTPS 포트 열기
EXPOSE 8080
EXPOSE 8443

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]

