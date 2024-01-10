FROM eclipse-temurin:21 as build
WORKDIR /app
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY jte jte
COPY common common
COPY kora-blocking kora-blocking
RUN ls -la .
RUN ./gradlew clean :kora-blocking:distTar

FROM eclipse-temurin:21
WORKDIR /
COPY --from=build /app/kora-blocking/build/distributions/app.tar app.tar
RUN tar -xvf app.tar
ENV JAVA_OPTS "-XX:+UseNUMA --enable-preview"
EXPOSE 8080
CMD ["/app/bin/app"]
