package service.ktorfit

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import model.User

interface KtorFitRest {
    @GET("users")
    suspend fun getAll(): List<User>

    @GET("users/{id}")
    suspend fun getById(@Path("id") id: Int): User?
}