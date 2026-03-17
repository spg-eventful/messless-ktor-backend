package at.eventful.messless.services.auth.commands

import kotlinx.serialization.Serializable

@Serializable
data class CreateAuthJWTCmd(
    val jwt: String,
)