package ru.tinkoff.kora.techempower.loom.nima.nima;

import io.helidon.http.HeaderNames;
import io.helidon.http.ServerRequestHeaders;
import jakarta.annotation.Nullable;
import ru.tinkoff.kora.http.common.header.HttpHeaders;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class NimaHeaders implements HttpHeaders {
    private final ServerRequestHeaders headers;

    public NimaHeaders(ServerRequestHeaders headers) {
        this.headers = headers;
    }

    @Nullable
    @Override
    public String getFirst(String name) {
        return headers.value(HeaderNames.create(name)).orElse(null);
    }

    @Nullable
    @Override
    public List<String> getAll(String name) {
        return headers.values(HeaderNames.create(name));
    }

    @Override
    public boolean has(String key) {
        return headers.contains(HeaderNames.create(key));
    }

    @Override
    public int size() {
        return this.headers.size();
    }

    @Override
    public Set<String> names() {
        return this.headers.toMap().keySet();
    }

    @Override
    public Iterator<Map.Entry<String, List<String>>> iterator() {
        var i = headers.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public Map.Entry<String, List<String>> next() {
                var header = i.next();
                return Map.entry(header.name(), header.allValues());
            }
        };
    }
}
