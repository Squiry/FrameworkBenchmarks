package ru.tinkoff.kora.techempower.loom.undertow;

import ru.tinkoff.kora.database.common.annotation.Batch;
import ru.tinkoff.kora.database.common.annotation.Query;
import ru.tinkoff.kora.database.common.annotation.Repository;
import ru.tinkoff.kora.database.jdbc.JdbcRepository;
import ru.tinkoff.kora.techempower.common.Fortune;
import ru.tinkoff.kora.techempower.common.World;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface TechempowerRepository extends JdbcRepository {

    @Query("SELECT id, randomNumber random_number FROM world WHERE id = :id")
    World findWorldById(int id);

    @Query("SELECT id, randomNumber random_number FROM world WHERE id = :id")
    World findWorldByIdWithConnection(Connection connection, int id);

    @Query("SELECT id, message FROM fortune")
    List<Fortune> fortunes();

    @Query("UPDATE world SET randomnumber = :world.randomNumber WHERE id = :world.id")
    void update(@Batch List<World> world);

    @Query("UPDATE world SET randomnumber = :world.randomNumber WHERE id = :world.id")
    void updateWithConnection(Connection connection, @Batch List<World> world);

    @Query("UPDATE world SET randomnumber = :world.randomNumber WHERE id = :world.id")
    void updateWithConnection(Connection connection, World world);
}
