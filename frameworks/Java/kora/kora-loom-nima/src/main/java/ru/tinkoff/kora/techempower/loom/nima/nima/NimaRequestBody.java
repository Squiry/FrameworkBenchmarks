package ru.tinkoff.kora.techempower.loom.nima.nima;

import io.helidon.webserver.http.ServerRequest;
import jakarta.annotation.Nullable;
import ru.tinkoff.kora.common.Context;
import ru.tinkoff.kora.common.util.FlowUtils;
import ru.tinkoff.kora.http.common.body.HttpBodyInput;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

public class NimaRequestBody implements HttpBodyInput {
    private final ServerRequest req;

    public NimaRequestBody(ServerRequest req) {
        this.req = req;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
        FlowUtils.<ByteBuffer>error(Context.current(), new RuntimeException("TODO")).subscribe(subscriber);
        //todo
    }

    @Nullable
    @Override
    public InputStream asInputStream() {
        return req.content().inputStream();
    }

    @Override
    public int contentLength() {
        return (int) req.headers().contentLength().orElse(-1L);
    }

    @Nullable
    @Override
    public String contentType() {
        return req.headers().contentType().map(v -> v.mediaType().text()).orElse(null);
    }

    @Override
    public void close() throws IOException {
        req.content().consume();
    }
}
