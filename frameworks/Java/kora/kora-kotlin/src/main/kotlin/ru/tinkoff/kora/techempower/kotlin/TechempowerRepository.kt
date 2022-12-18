package ru.tinkoff.kora.techempower.kotlin

import ru.tinkoff.kora.database.common.annotation.Batch
import ru.tinkoff.kora.database.common.annotation.Query
import ru.tinkoff.kora.database.common.annotation.Repository
import ru.tinkoff.kora.database.vertx.VertxRepository

@Repository
interface TechempowerRepository : VertxRepository {
    @Query("SELECT id, randomNumber random_number FROM world WHERE id = :id")
    suspend fun findWorldById(id: Int): World

    @Query("SELECT id, message FROM fortune")
    suspend fun fortunes(): List<Fortune>

    @Query("UPDATE world SET randomnumber = :world.randomNumber WHERE id = :world.id")
    suspend fun updateList(@Batch world: List<World>)
}
