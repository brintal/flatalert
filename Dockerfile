FROM openjdk:11.0.7-jre-slim
EXPOSE 8080
WORKDIR /var/flatalert/
ENTRYPOINT ["java", "-jar", "/var/flatalert/flatalert.jar"]
ARG JAR_FILE
ADD target/${JAR_FILE} /var/flatalert/flatalert.jar