# 베이스 이미지로 OpenJDK 사용
FROM openjdk:21-jdk-slim AS build

# 작업 디렉토리 생성
WORKDIR /app

# Gradle Wrapper와 필요한 설정 파일 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY src src

# Gradle을 사용하여 애플리케이션 빌드
RUN chmod +x ./gradlew && ./gradlew build -x test

# 실행 환경 설정
FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드한 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]