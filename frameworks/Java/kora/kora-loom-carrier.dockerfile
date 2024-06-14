FROM eclipse-temurin:22 as build
WORKDIR /app
COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties gradlew ./
COPY jte jte
COPY common common
COPY kora-loom-undertow kora-loom-undertow
RUN ls -la .
RUN ./gradlew clean :kora-loom-undertow:distTar

FROM eclipse-temurin:22
WORKDIR /
COPY --from=build /app/kora-loom-undertow/build/distributions/app.tar app.tar
RUN tar -xvf app.tar
ENV JAVA_OPTS "-XX:+UseNUMA --enable-preview --add-exports java.base/jdk.internal.misc=ALL-UNNAMED"
ENV POOL_MODE "CARRIER"
EXPOSE 8080
CMD ["/app/bin/app"]
