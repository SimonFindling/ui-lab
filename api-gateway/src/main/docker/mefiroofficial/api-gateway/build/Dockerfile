FROM frolvlad/alpine-oraclejdk8:slim
EXPOSE 8081
COPY maven /
RUN sh -c 'touch /app.jar'
VOLUME ["/tmp"]
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
