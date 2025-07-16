FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
RUN ls -la /app/target/

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/YellowHeart_BE-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]