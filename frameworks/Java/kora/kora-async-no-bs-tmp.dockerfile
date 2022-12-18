FROM eclipse-temurin:19
WORKDIR /app
COPY kora-async-no-bs/build/distributions/kora-async-no-bs.tar kora-async-no-bs.tar
RUN tar -xvf kora-async-no-bs.tar
ENV JAVA_OPTS "-XX:+UseNUMA"
EXPOSE 8080
CMD ["/app/kora-async-no-bs/bin/kora-async-no-bs"]
