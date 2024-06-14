FROM eclipse-temurin:22 as build
WORKDIR /app
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY jte jte
COPY common common
COPY kora-loom-undertow kora-loom-undertow
RUN ls -la .
RUN ./gradlew clean :kora-loom-undertow:distTar

FROM eclipse-temurin:22
WORKDIR /
COPY --from=build /app/kora-loom-undertow/build/distributions/app.tar app.tar
RUN tar -xvf app.tar
ENV JAVA_OPTS "-XX:+UseNUMA --enable-preview"
ENV POOL_MODE "DEFAULT"
EXPOSE 8080
CMD ["/app/bin/app"]
