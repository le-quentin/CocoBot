FROM eclipse-temurin:17.0.2_8-jre

WORKDIR /app
COPY ./build/libs/CocoBot-1.0-SNAPSHOT-all.jar ./coco.jar
VOLUME /app/data

CMD ["java", "-jar", "./coco.jar"]