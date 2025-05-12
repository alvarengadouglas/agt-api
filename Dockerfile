FROM adoptopenjdk/openjdk11:jre-11.0.9_11.1-alpine
VOLUME /tmp
COPY target/*.jar app.jar
COPY newrelic ./newrelic
RUN apk add --no-cache tzdata
ENV TZ="America/Sao_Paulo"
ENTRYPOINT exec java $JAVA_OPTS  -jar /app.jar
