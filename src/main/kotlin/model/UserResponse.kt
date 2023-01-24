package model

sealed class UserResponse<T>

class UserResponseSuccess<T: Any>(val code: Int, val data: T) : UserResponse<T>()

class UserResponseError(val code: Int, val message: String?) : UserResponse<Nothing>()