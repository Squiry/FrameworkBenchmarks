package ru.tinkoff.kora.techempower.loom.nima.nima;

import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.common.Context;
import ru.tinkoff.kora.http.server.common.handler.BlockingRequestExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Component
public final class NimaBlockingRequestExecutor implements BlockingRequestExecutor {
    @Override
    public <T> CompletionStage<T> execute(Context context, Callable<T> handler) {
        try {
            var r = handler.call();
            return CompletableFuture.completedFuture(r);
        } catch (Throwable t) {
            return CompletableFuture.failedFuture(t);
        }
    }
}
