package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class User(
    @BsonId
    val id: Int,
    @SerialName("first_name")
    val nombre: String,
    @SerialName("email")
    val mail: String
)
