package ru.tinkoff.kora.techempower.blocking;

import ru.tinkoff.kora.application.graph.KoraApplication;
import ru.tinkoff.kora.common.KoraApp;
import ru.tinkoff.kora.config.hocon.HoconConfigModule;
import ru.tinkoff.kora.database.jdbc.JdbcDatabaseModule;
import ru.tinkoff.kora.http.server.undertow.UndertowHttpServerModule;
import ru.tinkoff.kora.json.module.JsonModule;
import ru.tinkoff.kora.jte.common.JteModule;
import ru.tinkoff.kora.logging.logback.LogbackModule;

@KoraApp
public interface Application extends UndertowHttpServerModule, JdbcDatabaseModule, HoconConfigModule, JsonModule, JteModule, LogbackModule {
    static void main(String[] args) {
        KoraApplication.run(ApplicationGraph::graph);
    }
}
