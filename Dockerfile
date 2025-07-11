# 1단계: 빌드 스테이지
FROM openjdk:21-jdk-slim AS build

WORKDIR /app

#  의존성 관련 파일 먼저 복사 (변경 안 되면 캐시 그대로 유지)
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY gradlew gradlew
COPY gradle gradle

#  의존성만 먼저 다운로드
RUN chmod +x ./gradlew && ./gradlew dependencies

# 👉이후 소스 복사 (변경 자주 발생)
COPY src src

# 👉애플리케이션 빌드
RUN ./gradlew build -x test

# 2단계: 실행 스테이지
FROM openjdk:21-jdk-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
