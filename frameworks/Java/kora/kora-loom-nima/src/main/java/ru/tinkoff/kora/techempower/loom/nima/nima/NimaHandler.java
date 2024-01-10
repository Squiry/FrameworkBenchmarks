package ru.tinkoff.kora.techempower.loom.nima.nima;

import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import ru.tinkoff.kora.common.Context;
import ru.tinkoff.kora.http.server.common.router.PublicApiHandler;

public final class NimaHandler implements Handler {
    private static final ThreadLocal<byte[]> BUF = ThreadLocal.withInitial(() -> new byte[1024]);
    private final PublicApiHandler publicApiHandler;

    public NimaHandler(PublicApiHandler publicApiHandler) {this.publicApiHandler = publicApiHandler;}

    @Override
    public void handle(ServerRequest req, ServerResponse res) throws Exception {
        var ctx = Context.clear();
        var rs = publicApiHandler.process(ctx, new NimaRequest(req));
        var httpRs = rs.response().get();

        res.status(httpRs.code());
        for (var header : httpRs.headers()) {
            res.header(header.getKey(), header.getValue().toArray(new String[0]));
        }

        try (var content = httpRs.body()) {
            if (content == null) {
                return;
            }
            var full = content.getFullContentIfAvailable();
            if (full == null) {
                try (var os = res.outputStream()) {
                    content.write(os);
                }
                return;
            }
            if (full.hasArray()) {
                if (full.arrayOffset() == 0 && full.remaining() == full.array().length) {
                    res.send(full.array());
                } else {
                    try (var os = res.outputStream()) {
                        os.write(full.array(), full.arrayOffset(), full.remaining());
                    }
                }
            } else {
                try (var os = res.outputStream()) {
                    var buf = BUF.get();
                    while (full.hasRemaining()) {
                        var read = Math.min(buf.length, full.remaining());
                        full.get(buf, 0, read);
                        os.write(buf, 0, read);
                    }
                }
            }

        }
    }
}
