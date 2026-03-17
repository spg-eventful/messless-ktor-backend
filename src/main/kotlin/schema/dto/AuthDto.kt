package at.eventful.messless.schema.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthDto(
    val jwt: String,
    val user: UserDto,
)