package ru.tinkoff.kora.jte.common;

import gg.jte.output.Utf8ByteOutput;
import jakarta.annotation.Nullable;
import ru.tinkoff.kora.common.Context;
import ru.tinkoff.kora.http.common.body.HttpBody;
import ru.tinkoff.kora.http.server.common.HttpServerRequest;
import ru.tinkoff.kora.http.server.common.HttpServerResponse;
import ru.tinkoff.kora.http.server.common.handler.HttpServerResponseMapper;

import java.io.IOException;
import java.nio.ByteBuffer;

public class JteTemplateHttpServerResponseMapper<T> implements HttpServerResponseMapper<T> {
    private final JteTemplateWriter<T> templateWriter;

    public JteTemplateHttpServerResponseMapper(JteTemplateWriter<T> templateWriter) {
        this.templateWriter = templateWriter;
    }

    @Override
    public HttpServerResponse apply(Context ctx, HttpServerRequest request, @Nullable T result) throws IOException {
        try (var out = new Utf8ByteOutput(1024, 1024)) {
            this.templateWriter.write(result, out);
            var buf = ByteBuffer.allocate(out.getContentLength());
            out.writeTo(buf::put);
            return HttpServerResponse.of(200, HttpBody.of("text/html; charset=UTF-8", buf.flip()));
        }
    }

}
