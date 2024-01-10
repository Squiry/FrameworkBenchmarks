FROM eclipse-temurin:21
WORKDIR /app
COPY kora-blocking/build/distributions/kora-kotlin.tar kora-kotlin.tar
RUN tar -xvf kora-kotlin.tar
ENV JAVA_OPTS "-XX:+UseNUMA"
EXPOSE 8080
CMD ["/app/kora-kotlin/bin/kora-kotlin"]
