FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw package -DskipTests && cp -r target/*.jar app.jar
RUN find target/ -type f -name "*.jar" -exec cp {} app.jar \;
ENTRYPOINT ["java","-jar","/app/app.jar"]