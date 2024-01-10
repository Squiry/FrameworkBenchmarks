package ru.tinkoff.kora.techempower.loom.undertow;

import ru.tinkoff.kora.application.graph.KoraApplication;
import ru.tinkoff.kora.common.Context;
import ru.tinkoff.kora.common.DefaultComponent;
import ru.tinkoff.kora.common.KoraApp;
import ru.tinkoff.kora.config.hocon.HoconConfigModule;
import ru.tinkoff.kora.database.jdbc.JdbcDatabaseModule;
import ru.tinkoff.kora.http.server.common.handler.BlockingRequestExecutor;
import ru.tinkoff.kora.http.server.undertow.UndertowHttpServerModule;
import ru.tinkoff.kora.json.module.JsonModule;
import ru.tinkoff.kora.jte.common.JteModule;
import ru.tinkoff.kora.logging.logback.LogbackModule;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@KoraApp
public interface Application extends UndertowHttpServerModule, JdbcDatabaseModule, HoconConfigModule, JsonModule, JteModule, LogbackModule {
    static void main(String[] args) {
        KoraApplication.run(ApplicationGraph::graph);
    }

    default BlockingRequestExecutor loomblockingRequestExecutor() {
        var factory = Thread.ofVirtual().name("kora-request-", 1).factory();

        return new BlockingRequestExecutor() {
            @Override
            public <T> CompletionStage<T> execute(Context context, Callable<T> handler) {
                var f = new CompletableFuture<T>();
                factory.newThread(() -> {
                    context.inject();
                    try {
                        var r = handler.call();
                        f.complete(r);
                    } catch (Throwable e) {
                        f.completeExceptionally(e);
                    }
                }).start();
                return f;
            }
        };
    }

}
