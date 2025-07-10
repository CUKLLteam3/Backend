# Java 17 이미지 기반
FROM openjdk:21-jdk-slim

# JAR 파일 경로를 변수로 설정 (build/libs 내부의 JAR)
ARG JAR_FILE=build/libs/*.jar

# 복사해서 app.jar로 이름 바꿈
COPY ${JAR_FILE} app.jar

# 8080 포트 열기
EXPOSE 8080

# Spring Boot 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
