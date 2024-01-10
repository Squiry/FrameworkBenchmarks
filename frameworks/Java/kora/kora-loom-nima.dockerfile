FROM eclipse-temurin:21
WORKDIR /app
COPY kora-blocking/build/distributions/kora-loom-nima.tar kora-loom-nima.tar
RUN tar -xvf kora-loom-nima.tar
ENV JAVA_OPTS "-XX:+UseNUMA"
EXPOSE 8080
CMD ["/app/kora-loom-nima/bin/kora-loom-nima"]
