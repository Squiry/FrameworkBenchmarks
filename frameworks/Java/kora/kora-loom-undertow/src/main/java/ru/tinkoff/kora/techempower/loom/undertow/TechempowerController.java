package ru.tinkoff.kora.techempower.loom.undertow;

import jakarta.annotation.Nullable;
import ru.tinkoff.kora.http.common.annotation.HttpRoute;
import ru.tinkoff.kora.http.common.annotation.Query;
import ru.tinkoff.kora.http.common.body.HttpBody;
import ru.tinkoff.kora.http.server.common.HttpServerResponse;
import ru.tinkoff.kora.http.server.common.annotation.HttpController;
import ru.tinkoff.kora.json.common.annotation.Json;
import ru.tinkoff.kora.jte.common.Jte;
import ru.tinkoff.kora.techempower.common.Fortune;
import ru.tinkoff.kora.techempower.common.JsonResponse;
import ru.tinkoff.kora.techempower.common.World;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

@HttpController
public final class TechempowerController {
    private final TechempowerRepository repository;

    public TechempowerController(TechempowerRepository repository) {
        this.repository = repository;
    }


    @HttpRoute(method = "GET", path = "/json")
    @Json
    public JsonResponse json() {
        return new JsonResponse("Hello, World!");
    }

    @HttpRoute(method = "GET", path = "/db")
    @Json
    public World db() {
        var id = World.randomWorldNumber();
        return this.repository.findWorldById(id);
    }

    @HttpRoute(method = "GET", path = "/queries")
    @Json
    public List<World> queries(@Query("queries") @Nullable String queriesStr) {
        int queries = World.parseQueryCount(queriesStr);
        return this.repository.getJdbcConnectionFactory().withConnection(() -> {
            var result = new ArrayList<World>(queries);
            for (int i = 0; i < queries; i++) {
                var id = World.randomWorldNumber();
                result.add(this.repository.findWorldById(id));
            }
            return result;
        });
    }

    @HttpRoute(method = "GET", path = "/fortunes")
    @Jte
    public FortunesTemplate fortunes() {
        var fortunes = this.repository.fortunes();
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));
        fortunes.sort(comparing(Fortune::message));
        return new FortunesTemplate(fortunes);
    }

    @HttpRoute(method = "GET", path = "/updates")
    @Json
    public List<World> updates(@Query("queries") @Nullable String queriesStr) {
        int queries = World.parseQueryCount(queriesStr);
        var result = new ArrayList<World>(queries);
        this.repository.getJdbcConnectionFactory().withConnection(() -> {
            for (var i = 0; i < queries; i++) {
                var world = this.repository.findWorldById(World.randomWorldNumber());
                var newNumber = World.randomWorldNumber(world.randomNumber());
                result.add(new World(world.id(), newNumber));
            }
            result.sort(comparing(World::id));
            this.repository.update(result);
        });
        return result;
    }

    private static final ByteBuffer PLAINTEXT_RESPONSE = ByteBuffer.wrap("Hello, World!".getBytes(StandardCharsets.UTF_8));

    @HttpRoute(method = "GET", path = "/plaintext")
    public HttpServerResponse plaintext() {
        return HttpServerResponse.of(200, HttpBody.plaintext(PLAINTEXT_RESPONSE));
    }
}
