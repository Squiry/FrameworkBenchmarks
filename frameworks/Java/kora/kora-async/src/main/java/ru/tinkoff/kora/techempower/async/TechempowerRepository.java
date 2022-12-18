package ru.tinkoff.kora.techempower.async;

import io.vertx.sqlclient.SqlClient;
import ru.tinkoff.kora.database.common.annotation.Batch;
import ru.tinkoff.kora.database.common.annotation.Query;
import ru.tinkoff.kora.database.common.annotation.Repository;
import ru.tinkoff.kora.database.vertx.VertxRepository;
import ru.tinkoff.kora.techempower.common.Fortune;
import ru.tinkoff.kora.techempower.common.World;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public interface TechempowerRepository extends VertxRepository {
    @Query("SELECT id, randomNumber random_number FROM world WHERE id = :id")
    CompletableFuture<World> findWorldById(SqlClient connection, int id);

    @Query("SELECT id, message FROM fortune")
    CompletableFuture<List<Fortune>> fortunes(SqlClient connection);

    @Query("UPDATE world SET randomnumber = :world.randomNumber WHERE id = :world.id")
    CompletableFuture<Void> updateList(SqlClient client, @Batch List<World> world);
}
