package service.cache

import io.github.reactivecircus.cache4k.Cache
import model.User
import kotlin.time.Duration.Companion.seconds

class CacheUsers {
    val refreshTime = 60_000

    val cache = Cache.Builder()
        .expireAfterAccess(60.seconds)
        .maximumCacheSize(25)
        .build<Int, User>()
}