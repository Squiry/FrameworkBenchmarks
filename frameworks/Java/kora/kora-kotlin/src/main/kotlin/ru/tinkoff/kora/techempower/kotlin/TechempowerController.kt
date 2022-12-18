package ru.tinkoff.kora.techempower.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import ru.tinkoff.kora.common.Context
import ru.tinkoff.kora.database.vertx.VertxRepository
import ru.tinkoff.kora.http.common.HttpBody
import ru.tinkoff.kora.http.common.annotation.HttpRoute
import ru.tinkoff.kora.http.common.annotation.Query
import ru.tinkoff.kora.http.server.common.HttpServerResponse
import ru.tinkoff.kora.http.server.common.annotation.HttpController
import ru.tinkoff.kora.json.common.annotation.Json
import ru.tinkoff.kora.jte.common.Jte
import ru.tinkoff.kora.techempower.common.World.*
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import kotlin.coroutines.coroutineContext

@HttpController
class TechempowerController(private val repository: TechempowerRepository) {

    private val plaintextResponse = ByteBuffer.wrap("Hello, World!".toByteArray(StandardCharsets.UTF_8))

    @HttpRoute(method = "GET", path = "/plaintext")
    suspend fun plaintext() = HttpServerResponse.of(200, HttpBody.plaintext(plaintextResponse))!!

    @HttpRoute(method = "GET", path = "/json")
    @Json
    suspend fun json() = JsonResponse("Hello, World!")

    @HttpRoute(method = "GET", path = "/db")
    @Json
    suspend fun db(): World = repository.findWorldById(randomWorldNumber())

    @HttpRoute(method = "GET", path = "/queries")
    @Json
    suspend fun queries(@Query("queries") queriesStr: String?): List<World> {
        val queries = parseQueryCount(queriesStr)
        return repository.withConnection {
            val result = ArrayList<Deferred<World>>(queries)
            for (i in 0 until queries) {
                val id = randomWorldNumber()
                result.add(async { repository.findWorldById(id) })
            }
            result.map { it.await() }
        }
    }

    @HttpRoute(method = "GET", path = "/updates")
    @Json
    suspend fun update(@Query("queries") queriesStr: String?): List<World> {
        val queries = parseQueryCount(queriesStr)
        return repository.withConnection {
            val result = ArrayList<Deferred<World>>(queries)
            for (i in 0 until queries) {
                val id = randomWorldNumber()
                result.add(async { repository.findWorldById(id) })
            }
            val newWorlds = result.map {
                val world = it.await()
                World(world.id, randomWorldNumber(world.randomNumber))
            }
            repository.updateList(newWorlds)
            newWorlds
        }
    }

    @HttpRoute(method = "GET", path = "/fortunes")
    @Jte
    suspend fun fortunes(): FortunesTemplate {
        val fortunes = repository.fortunes() as MutableList<Fortune>
        fortunes.add(Fortune(0, "Additional fortune added at request time."))
        fortunes.sortBy { it.message }
        return FortunesTemplate(fortunes)
    }

    private suspend fun <T> VertxRepository.withConnection(callback: suspend CoroutineScope.() -> T): T {
        val repository = this
        val scope = CoroutineScope(coroutineContext + Dispatchers.Unconfined)
        val future = repository.vertxConnectionFactory.withConnectionCompletionStage {
            scope.future { callback(scope) }
        }
        return future.await()
    }

}
