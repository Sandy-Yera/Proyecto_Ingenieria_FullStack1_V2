#Version java en la que se compiló
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
#El nombre lo podemos conseguir de la carpeta target (tras copilar)
COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
