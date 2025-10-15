### Use a lightweight base image
##FROM openjdk:17-jdk-slim
##
### Set working directory
##WORKDIR /app
##
### Copy the JAR file into the container
##COPY target/*.jar app.jar
##
### Expose port 8080
##EXPOSE 8080
##
### Run the JAR file
##ENTRYPOINT ["java", "-jar", "app.jar"]
#
#FROM openjdk:17-jdk-slim
#
#COPY target/*.jar AuthSync-0.0.1-SNAPSHOT.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java","-jar","/AuthSync-0.0.1-SNAPSHOT.jar"]
#
