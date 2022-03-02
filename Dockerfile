FROM eclipse-temurin:17

RUN mkdir /opt/app
COPY ./build/libs/CocoBot-1.0-SNAPSHOT-all.jar /opt/app/coco.jar
COPY ./stored_messages /opt/app
WORKDIR /opt/app

CMD ["java", "-jar", "./coco.jar"]