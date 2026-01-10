# Multi-stage build для мінімального розміру образу

# Етап 1: Build (збірка JAR файлу)
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Копіюємо тільки pom.xml спочатку (для кешування залежностей)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копіюємо решту коду
COPY src ./src

# Збираємо проєкт (пропускаємо тести для швидкості)
RUN mvn clean package -DskipTests

# Етап 2: Runtime (запуск додатку)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Копіюємо тільки JAR з етапу build
COPY --from=build /app/target/*.jar app.jar

# Відкриваємо порт
EXPOSE 8080

# Запускаємо додаток
ENTRYPOINT ["java", "-jar", "app.jar"]
