package ru.tinkoff.kora.techempower.loom.undertow;

import io.undertow.connector.ByteBufferPool;
import ru.tinkoff.kora.application.graph.KoraApplication;
import ru.tinkoff.kora.common.Context;
import ru.tinkoff.kora.common.KoraApp;
import ru.tinkoff.kora.config.common.annotation.ConfigSource;
import ru.tinkoff.kora.config.hocon.HoconConfigModule;
import ru.tinkoff.kora.database.jdbc.JdbcDatabaseModule;
import ru.tinkoff.kora.http.server.common.handler.BlockingRequestExecutor;
import ru.tinkoff.kora.json.module.JsonModule;
import ru.tinkoff.kora.jte.common.JteModule;
import ru.tinkoff.kora.logging.logback.LogbackModule;
import ru.tinkoff.kora.techempower.loom.undertow.pool.CarrierThreadLocalPool;
import ru.tinkoff.kora.techempower.loom.undertow.pool.ThreadLocalPool;
import ru.tinkoff.kora.techempower.loom.undertow.pool.VThreadNoopPool;
import ru.tinkoff.kora.techempower.loom.undertow.undertow.UndertowHttpServerModule;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

@KoraApp
public interface Application extends UndertowHttpServerModule, JdbcDatabaseModule, HoconConfigModule, JsonModule, JteModule, LogbackModule {
    static void main(String[] args) {
        KoraApplication.run(ApplicationGraph::graph);
        JsonModule.JSON_FACTORY._getBufferRecycler();
        JsonModule.JSON_FACTORY._getRecyclerPool();
    }

    default BlockingRequestExecutor loomblockingRequestExecutor() {
        var factory = Thread.ofVirtual().name("kora-request-", 1).factory();
        var executor = (Consumer<Runnable>) r -> factory.newThread(r).start();

        return new BlockingRequestExecutor() {
            @Override
            public <T> CompletionStage<T> execute(Context context, Callable<T> handler) {
                return BlockingRequestExecutor.defaultExecute(context, executor, handler);
            }
        };
    }

    @ConfigSource("pool")
    interface PoolConfig {
        Mode mode();

        enum Mode {
            DEFAULT, NOOP, CARRIER
        }
    }

    default ByteBufferPool undertowPool(PoolConfig config) {
        return switch (config.mode()) {
            case DEFAULT -> new ThreadLocalPool(true);
            case NOOP -> new VThreadNoopPool(true);
            case CARRIER -> new CarrierThreadLocalPool(true);
        };
    }
}
