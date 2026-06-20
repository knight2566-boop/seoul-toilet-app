FROM node:20-alpine AS frontend-build
WORKDIR /frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /backend
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
COPY --from=frontend-build /frontend/dist ./src/main/resources/static
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend-build /backend/target/seoul-toilet-backend-1.0.0.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=aws"]
