FROM openjdk:8-alpine

COPY target/uberjar/flash-spike.jar /flash-spike/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/flash-spike/app.jar"]
