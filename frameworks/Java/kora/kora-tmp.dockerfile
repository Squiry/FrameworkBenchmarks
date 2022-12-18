FROM eclipse-temurin:19
WORKDIR /app
COPY kora-blocking/build/distributions/kora-blocking.tar kora-blocking.tar
RUN tar -xvf kora-blocking.tar
ENV JAVA_OPTS "-XX:+UseNUMA"
EXPOSE 8080
CMD ["/app/kora-blocking/bin/kora-blocking"]
