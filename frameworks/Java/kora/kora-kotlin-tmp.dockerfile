FROM eclipse-temurin:19
WORKDIR /app
COPY kora-kotlin/build/distributions/kora-kotlin.tar kora-kotlin.tar
RUN tar -xvf kora-kotlin.tar
ENV JAVA_OPTS "-XX:+UseNUMA"
EXPOSE 8080
CMD ["/app/kora-kotlin/bin/kora-kotlin"]
