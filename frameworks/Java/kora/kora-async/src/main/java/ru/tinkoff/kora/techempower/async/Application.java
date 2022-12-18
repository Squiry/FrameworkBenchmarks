package ru.tinkoff.kora.techempower.async;

import io.netty.util.NettyRuntime;
import ru.tinkoff.kora.application.graph.KoraApplication;
import ru.tinkoff.kora.common.KoraApp;
import ru.tinkoff.kora.common.Tag;
import ru.tinkoff.kora.config.common.ConfigModule;
import ru.tinkoff.kora.database.vertx.VertxDatabaseModule;
import ru.tinkoff.kora.http.server.undertow.UndertowHttpServerModule;
import ru.tinkoff.kora.json.module.JsonModule;
import ru.tinkoff.kora.jte.common.JteModule;
import ru.tinkoff.kora.logging.logback.LogbackModule;
import ru.tinkoff.kora.netty.common.NettyCommonModule;


@KoraApp
public interface Application extends UndertowHttpServerModule, VertxDatabaseModule, ConfigModule, JsonModule, JteModule, LogbackModule {

    @Tag(NettyCommonModule.class)
    default Integer size() {
        return NettyRuntime.availableProcessors();
    }

    static void main(String[] args) {
        KoraApplication.run(ApplicationGraph::graph);
    }
}
