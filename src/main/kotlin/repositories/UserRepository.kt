package repositories

import db.DBManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import model.User
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import service.cache.CacheUsers
import service.ktorfit.KtorFitClient

class UserRepository(private val cache: CacheUsers) : ICRUDRepository<User, Int> {
    private val client by lazy { KtorFitClient.instance }

    private var refreshJob: Job? = null

    private var listSearches = mutableListOf<User>()

    init {
        refreshCache()
    }

    override suspend fun findAll(): Flow<User> = withContext(Dispatchers.IO) {
        var result = cache.cache.asMap().values.asFlow()
        if (result.toList().isEmpty()) { result = DBManager.database.getCollection<User>().find().asFlow() }
        if (result.toList().isEmpty()) { result = client.getAll().asFlow() }

        result
    }

    override suspend fun delete(entity: User): Boolean = withContext(Dispatchers.IO) {
        var exists = false

        val user = cache.cache.asMap()[entity.id]
        if (user != null) {
            listSearches.removeIf { it.id == user.id }
            cache.cache.invalidate(entity.id)
            DBManager.database.getCollection<User>().deleteOneById(entity.id)
            exists = true
        } else if (DBManager.database.getCollection<User>().deleteOneById(entity.id).equals(entity)) {
            exists = true
        }
        exists
    }

    override suspend fun save(entity: User): User = withContext(Dispatchers.IO) {
        listSearches.add(entity)
        DBManager.database.getCollection<User>().save(entity)
        entity
    }

    override suspend fun findById(id: Int): User? = withContext(Dispatchers.IO) {
        var result: User? = null

        cache.cache.asMap().forEach { if (it.key == id) result = it.value }

        result ?: DBManager.database.getCollection<User>().findOneById(id) ?: client.getById(id)
    }

    private fun refreshCache() {
        if (refreshJob != null) refreshJob?.cancel()

        refreshJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (listSearches.isNotEmpty()) {
                    listSearches.forEach {
                        cache.cache.put(it.id, it)
                    }

                    listSearches.clear()
                }

                delay(cache.refreshTime.toLong())
            }
        }
    }
}