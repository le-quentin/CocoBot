FROM eclipse-temurin:17.0.2_8-jre

WORKDIR /app
VOLUME /app/data
COPY ./build/libs/CocoBot-1.0-SNAPSHOT-all.jar ./coco.jar

CMD ["java", "-jar", "./coco.jar"]