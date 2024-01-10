package ru.tinkoff.kora.techempower.loom.nima.nima;

import io.helidon.webserver.WebServer;
import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.common.annotation.Root;
import ru.tinkoff.kora.http.server.common.HttpServer;
import ru.tinkoff.kora.http.server.common.HttpServerConfig;
import ru.tinkoff.kora.http.server.common.router.PublicApiHandler;

@Component
@Root
public final class NimaHttpServer implements HttpServer {
    private final HttpServerConfig config;
    private final PublicApiHandler publicApiHandler;
    private final WebServer server;

    public NimaHttpServer(PublicApiHandler publicApiHandler, HttpServerConfig config) {
        this.publicApiHandler = publicApiHandler;
        this.config = config;
        this.server = WebServer.builder()
            .port(config.publicApiHttpPort())
            .routing(b -> b.any(new NimaHandler(publicApiHandler)))
            .build();
    }

    @Override
    public int port() {
        return server.port();
    }

    @Override
    public void init() throws Exception {
        server.start();
    }

    @Override
    public void release() throws Exception {
        server.stop();
    }

}
