FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /usr/local/app
COPY ./build/libs/server-fat-1.0.jar ./server-fat-1.0.jar
EXPOSE 8181
CMD ["java", "-jar", "server-fat-1.0.jar"]
