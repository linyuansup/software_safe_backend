FROM maven:latest AS builder
COPY . .
COPY settings.xml /usr/share/maven/conf/settings.xml
RUN mvn package -f pom.xml

FROM openjdk:17
COPY --from=builder target/community-0.0.1-SNAPSHOT.jar .
RUN mkdir storage
RUN cd storage
RUN mkdir video
RUN mkdir image
RUN cd ../
EXPOSE 8080
ENTRYPOINT [ "java","-jar", "community-0.0.1-SNAPSHOT.jar" ]