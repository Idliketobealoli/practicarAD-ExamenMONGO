package controllers

import kotlinx.coroutines.flow.toList
import model.User
import model.UserResponse
import model.UserResponseError
import model.UserResponseSuccess
import repositories.UserRepository

class UserController(private val repo: UserRepository) {
    suspend fun findAll() : UserResponse<out List<User>> {
        val users = repo.findAll().toList()
        return if (users.isNotEmpty()) {
            UserResponseSuccess(200, users)
        }
        else UserResponseError(404, "Users not found")
    }

    suspend fun findById(id: Int) : UserResponse<out User> {
        return repo.findById(id)?.let { UserResponseSuccess(200, it) }
            .run { UserResponseError(404, "User with id $id not found.") }
    }

    suspend fun create(user: User) : UserResponse<out User> {
        val result = repo.save(user)
        return UserResponseSuccess(201, result)
    }

    suspend fun delete(user: User) : UserResponse<out Boolean> {
        val result = repo.delete(user)
        return if (result) { UserResponseSuccess(200, result) }
        else { UserResponseError(404, "No se pudo borrar porque no se encuentra al usuario.") }
    }
}