# Use Maven to build the WAR file
FROM maven:3.8.5-openjdk-11-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package

# Use Tomcat to serve the WAR file
FROM tomcat:10-jdk11
WORKDIR /app

# Remove default webapps (optional)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy fabflix.war into the Tomcat webapps directory
COPY --from=builder /app/target/fabflix.war /usr/local/tomcat/webapps/fabflix.war

EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]