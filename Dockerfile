FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 80
ADD target/spring-boot-mvc-1.0.0.jar spring-boot-mvc-1.0.0.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /spring-boot-mvc-1.0.0.jar" ]