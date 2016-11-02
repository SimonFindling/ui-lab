FROM frolvlad/alpine-oraclejdk8:slim
EXPOSE 8761
COPY maven /
RUN sh -c 'touch /app.jar'
VOLUME ["/tmp"]
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
