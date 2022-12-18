# Spring MVC Benchmarking Test

This is the kora portion of a [benchmarking test suite](../) comparing a variety of web development platforms.

An embedded undertow is used for the web server, with nearly everything configured with default settings.
The only thing changed is Hikari can use up to (2 * cores count) connections (the default is 10).
See [About-Pool-Sizing](https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing)

There are two implementations :
* One with reactive Postgresql driver from Vertx. See [JdbcDbRepository](src/main/java/hello/JdbcDbRepository.java).
* One with Postgresql jdbc driver. See [MongoDbRepository](src/main/java/hello/MongoDbRepository.java).

### Plaintext Test

* [Plaintext test source](src/main/java/hello/HelloController.java)

### JSON Serialization Test

* [JSON test source](src/main/java/hello/HelloController.java)

### Database Query Test

* [Database Query test source](src/main/java/hello/HelloController.java)

### Database Queries Test

* [Database Queries test source](src/main/java/hello/HelloController.java)

### Database Update Test

* [Database Update test source](src/main/java/hello/HelloController.java)

### Template rendering Test

* [Template rendering test source](src/main/java/hello/HelloController.java)

## Versions

* [OpenJDK Runtime Environment Temurin-19](https://adoptium.net/es/temurin/releases/?version=19)
* [Kora 0.10.0](http://github.com/Tinkoff/kora)

## Test URLs

### Plaintext Test

    http://localhost:8080/plaintext

### JSON Encoding Test

    http://localhost:8080/json

### Database Query Test

    http://localhost:8080/db

### Database Queries Test

    http://localhost:8080/queries?queries=5

### Database Update Test

    http://localhost:8080/updates?queries=5

### Template rendering Test

    http://localhost:8080/fortunes
