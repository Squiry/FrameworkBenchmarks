package ru.tinkoff.kora.techempower.async;

import io.netty.channel.EventLoopGroup;
import io.vertx.pgclient.PgConnection;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.impl.SqlClientInternal;
import ru.tinkoff.kora.database.vertx.VertxDatabaseConfig;
import ru.tinkoff.kora.http.common.HttpBody;
import ru.tinkoff.kora.http.common.annotation.HttpRoute;
import ru.tinkoff.kora.http.common.annotation.Query;
import ru.tinkoff.kora.http.server.common.HttpServerResponse;
import ru.tinkoff.kora.http.server.common.annotation.HttpController;
import ru.tinkoff.kora.json.common.annotation.Json;
import ru.tinkoff.kora.jte.common.Jte;
import ru.tinkoff.kora.techempower.common.Fortune;
import ru.tinkoff.kora.techempower.common.JsonResponse;
import ru.tinkoff.kora.techempower.common.World;
import ru.tinkoff.kora.vertx.common.VertxUtil;

import javax.annotation.Nullable;
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
    private final ThreadLocal<SqlClient> threadLocalClient;

    public TechempowerController(TechempowerRepository repository, EventLoopGroup eventLoopGroup, VertxDatabaseConfig vertxDatabaseConfig) {
        this.repository = repository;
        // that code is a bullshit, but vertx does that and so does helidon
        var vertx = VertxUtil.customEventLoopVertx(eventLoopGroup);
        this.threadLocalClient = ThreadLocal.withInitial(() -> PgConnection.connect(vertx, vertxDatabaseConfig.toPgConnectOptions()).toCompletionStage().toCompletableFuture().join());
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
        return this.repository.findWorldById(this.threadLocalClient.get(), id);
    }

    @HttpRoute(method = "GET", path = "/queries")
    @Json
    public CompletableFuture<List<World>> queries(@Query("queries") @Nullable String queriesStr) {
        int queries = World.parseQueryCount(queriesStr);
        var f = new CompletableFuture<?>[queries];
        var f0 = new CompletableFuture<Void>();
        ((SqlClientInternal) this.threadLocalClient.get()).group(client -> {
            for (int i = 0; i < queries; i++) {
                var id = World.randomWorldNumber();
                f[i] = this.repository.findWorldById(client, id);
            }
            f0.complete(null);
        });
        return f0.thenCompose(v -> CompletableFuture.allOf(f)).thenApply(v -> {
            var list = new ArrayList<World>(queries);
            for (var future : f) {
                list.add((World) future.resultNow());
            }
            return list;
        });
    }

    @HttpRoute(method = "GET", path = "/updates")
    @Json
    public CompletableFuture<List<World>> updates(@Query("queries") @Nullable String queriesStr) {
        int queries = World.parseQueryCount(queriesStr);
        var f = new CompletableFuture<?>[queries];
        var client = this.threadLocalClient.get();
        var f0 = new CompletableFuture<Void>();
        ((SqlClientInternal) client).group(c -> {
            for (int i = 0; i < queries; i++) {
                var id = World.randomWorldNumber();
                f[i] = this.repository.findWorldById(c, id);
            }
            f0.complete(null);
        });

        return CompletableFuture.allOf(f).thenCompose(v -> {
            var list = new ArrayList<World>(queries);
            for (var future : f) {
                var world = ((World) future.resultNow());
                var newNumber = World.randomWorldNumber(world.randomNumber());
                var newWorld = new World(world.id(), newNumber);
                list.add(newWorld);
            }
            list.sort(comparing(World::id));
            return this.repository.updateList(client, list).thenApply(v0 -> list);
        });
    }

    @HttpRoute(method = "GET", path = "/fortunes")
    @Jte
    public CompletionStage<FortunesTemplate> fortunes() {
        return this.repository.fortunes(this.threadLocalClient.get()).thenApply(l -> {
            l.add(new Fortune(0, "Additional fortune added at request time."));
            l.sort(comparing(Fortune::message));
            return new FortunesTemplate(l);
        });
    }
}
