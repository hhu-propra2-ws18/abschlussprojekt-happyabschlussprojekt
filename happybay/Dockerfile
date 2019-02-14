FROM openjdk:8-jdk-alpine AS build
WORKDIR /person-scs
COPY . ./
RUN ./gradlew --no-daemon --stacktrace clean bootJar

FROM openjdk:8-jre-alpine
RUN apk add --no-cache bash
WORKDIR /happybay-scs

COPY wait-for-it.sh .
COPY --from=build /happybay-scs/build/libs/*.jar happybay-scs.jar

CMD java -jar happybay-scs.jar
