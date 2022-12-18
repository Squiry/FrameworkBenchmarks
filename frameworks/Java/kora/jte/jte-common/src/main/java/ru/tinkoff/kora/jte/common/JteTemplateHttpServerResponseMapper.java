package ru.tinkoff.kora.jte.common;

import gg.jte.output.Utf8ByteOutput;
import ru.tinkoff.kora.http.common.HttpBody;
import ru.tinkoff.kora.http.server.common.HttpServerResponse;
import ru.tinkoff.kora.http.server.common.handler.HttpServerResponseMapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class JteTemplateHttpServerResponseMapper<T> implements HttpServerResponseMapper<T> {
    private final JteTemplateWriter<T> templateWriter;

    public JteTemplateHttpServerResponseMapper(JteTemplateWriter<T> templateWriter) {
        this.templateWriter = templateWriter;
    }

    @Override
    public java.util.concurrent.CompletableFuture<HttpServerResponse> apply(T result) {
        try (var out = new Utf8ByteOutput(1024, 1024)) {
            this.templateWriter.write(result, out);
            var buf = ByteBuffer.allocate(out.getContentLength());
            out.writeTo(buf::put);
            return CompletableFuture.completedFuture(HttpServerResponse.of(200, HttpBody.of("text/html; charset=UTF-8", buf.flip())));
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
