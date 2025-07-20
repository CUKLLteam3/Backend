# 1단계: 빌드 스테이지
FROM openjdk:21-slim AS build

WORKDIR /app

# 의존성 파일 먼저 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 의존성 캐시 다운로드
RUN chmod +x ./gradlew && ./gradlew dependencies

# 애플리케이션 소스 복사
COPY src ./src

# ✅ 인증서와 설정파일도 빌드 스테이지에 복사 (classpath 포함 위해)
COPY src/main/resources/application.yml ./src/main/resources/application.yml
COPY src/main/resources/mycert.p12 ./src/main/resources/mycert.p12

# 빌드 수행 (테스트 제외)
RUN ./gradlew build -x test

# 2단계: 실행 스테이지
FROM openjdk:21-jdk-slim

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# ✅ classpath 인식을 위해 JAR 옆에 인증서 복사
COPY --from=build /app/src/main/resources/mycert.p12 ./mycert.p12

# HTTPS 포트 열기
EXPOSE 8080
EXPOSE 8443

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
