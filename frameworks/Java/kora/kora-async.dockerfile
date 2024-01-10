FROM eclipse-temurin:21
WORKDIR /app
COPY kora-blocking/build/distributions/kora-async.tar kora-async.tar
RUN tar -xvf kora-async.tar
ENV JAVA_OPTS "-XX:+UseNUMA"
EXPOSE 8080
CMD ["/app/kora-async/bin/kora-async"]
