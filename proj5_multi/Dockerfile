FROM maven:3.8.5-openjdk-11-slim AS builder

ARG MVN_PROFILE="default"
WORKDIR /app

COPY . .

RUN mvn clean package -P${MVN_PROFILE}

FROM tomcat:10-jdk11
WORKDIR /app

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=builder /app/target/fabflix.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
