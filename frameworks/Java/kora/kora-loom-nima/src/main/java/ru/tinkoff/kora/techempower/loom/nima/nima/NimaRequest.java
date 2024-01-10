package ru.tinkoff.kora.techempower.loom.nima.nima;

import io.helidon.webserver.http.ServerRequest;
import ru.tinkoff.kora.http.common.body.HttpBodyInput;
import ru.tinkoff.kora.http.common.header.HttpHeaders;
import ru.tinkoff.kora.http.server.common.router.PublicApiRequest;

import java.util.Collection;
import java.util.Map;

public final class NimaRequest implements PublicApiRequest {
    private final ServerRequest req;

    public NimaRequest(ServerRequest req) {
        this.req = req;
    }

    @Override
    public String method() {
        return this.req.prologue().method().text();
    }

    @Override
    public String path() {
        return this.req.path().path();
    }

    @Override
    public String hostName() {
        return this.req.authority();
    }

    @Override
    public String scheme() {
        return this.req.requestedUri().scheme();
    }

    @Override
    public HttpHeaders headers() {
        return new NimaHeaders(this.req.headers());
    }

    @Override
    public Map<String, ? extends Collection<String>> queryParams() {
        return req.query().toMap();
    }

    @Override
    public HttpBodyInput body() {
        return new NimaRequestBody(this.req);
    }
}
