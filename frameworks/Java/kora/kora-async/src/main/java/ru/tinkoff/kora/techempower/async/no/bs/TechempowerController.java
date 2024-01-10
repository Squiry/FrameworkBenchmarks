package ru.tinkoff.kora.techempower.async.no.bs;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.Comparator.comparing;

@HttpController
public final class TechempowerController {
    private final TechempowerRepository repository;

    public TechempowerController(TechempowerRepository repository) {
        this.repository = repository;
    }

    private static final ByteBuffer PLAINTEXT_RESPONSE = ByteBuffer.wrap("Hello, World!".getBytes(StandardCharsets.UTF_8));

    @HttpRoute(method = "GET", path = "/plaintext")
    public CompletableFuture<HttpServerResponse> plaintext() {
        return CompletableFuture.completedFuture(HttpServerResponse.of(200, HttpBody.plaintext(PLAINTEXT_RESPONSE)));
    }

    @HttpRoute(method = "GET", path = "/json")
    @Json
    public CompletionStage<JsonResponse> json() {
        return CompletableFuture.completedFuture(new JsonResponse("Hello, World!"));
    }

    @HttpRoute(method = "GET", path = "/db")
    @Json
    public CompletionStage<World> db() {
        var id = World.randomWorldNumber();
        return this.repository.findWorldById(id);
    }

    @HttpRoute(method = "GET", path = "/queries")
    @Json
    public CompletionStage<List<World>> queries(@Query("queries") @Nullable String queriesStr) {
        int queries = World.parseQueryCount(queriesStr);
        return this.repository.getVertxConnectionFactory().withConnection(_ -> {
            var f = new CompletableFuture<?>[queries];
            for (int i = 0; i < queries; i++) {
                var id = World.randomWorldNumber();
                f[i] = this.repository.findWorldById(id);
            }
            return CompletableFuture.allOf(f).thenApply(v -> {
                var list = new ArrayList<World>(queries);
                for (var future : f) {
                    list.add((World) future.resultNow());
                }
                return list;
            });
        });
    }

    @HttpRoute(method = "GET", path = "/updates")
    @Json
    public CompletionStage<List<World>> updates(@Query("queries") @Nullable String queriesStr) {
        int queries = World.parseQueryCount(queriesStr);
        return this.repository.getVertxConnectionFactory().withConnection(_ -> {
            var f = new CompletableFuture<?>[queries];
            for (int i = 0; i < queries; i++) {
                var id = World.randomWorldNumber();
                f[i] = this.repository.findWorldById(id);
            }

            return CompletableFuture.allOf(f).thenCompose(v -> {
                var list = new ArrayList<World>(queries);
                for (var future : f) {
                    var world = ((World) future.resultNow());
                    var newNumber = World.randomWorldNumber(world.randomNumber());
                    var newWorld = new World(world.id(), newNumber);
                    list.add(newWorld);
                }
                list.sort(comparing(World::id));
                return this.repository.updateList(list).thenApply(v0 -> list);
            });
        });
    }

    @HttpRoute(method = "GET", path = "/fortunes")
    @Jte
    public CompletionStage<FortunesTemplate> fortunes() {
        return this.repository.fortunes().thenApply(l -> {
            l.add(new Fortune(0, "Additional fortune added at request time."));
            l.sort(comparing(Fortune::message));
            return new FortunesTemplate(l);
        });
    }
}
