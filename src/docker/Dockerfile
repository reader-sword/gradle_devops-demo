FROM java:8-jdk-alpine
MAINTAINER lyy <lyy@163.com>

ENV TZ "Asia/Shanghai"

VOLUME /tmp
ADD *.jar  app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar