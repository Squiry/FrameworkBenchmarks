package ru.tinkoff.kora.techempower.loom.undertow.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import jakarta.annotation.Nullable;
import ru.tinkoff.kora.common.Context;
import ru.tinkoff.kora.http.server.common.router.PublicApiHandler;
import ru.tinkoff.kora.http.server.common.telemetry.HttpServerTracer;

public final class UndertowPublicApiHandler implements HttpHandler {
    private final PublicApiHandler publicApiHandler;
    @Nullable
    private final HttpServerTracer tracer;

    public UndertowPublicApiHandler(PublicApiHandler publicApiHandler, @Nullable HttpServerTracer tracer) {
        this.publicApiHandler = publicApiHandler;
        this.tracer = tracer;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        var context = Context.clear();
        var exchangeProcessor = new UndertowExchangeProcessor(exchange, this.publicApiHandler, context, this.tracer);
        exchange.dispatch(SameThreadExecutor.INSTANCE, exchangeProcessor);
    }
}
