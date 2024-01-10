FROM eclipse-temurin:21
WORKDIR /app
COPY kora-blocking/build/distributions/kora-loom-undertow.tar kora-loom-undertow.tar
RUN tar -xvf kora-loom-undertow.tar
ENV JAVA_OPTS "-XX:+UseNUMA"
EXPOSE 8080
CMD ["/app/kora-loom-undertow/bin/kora-loom-undertow"]
