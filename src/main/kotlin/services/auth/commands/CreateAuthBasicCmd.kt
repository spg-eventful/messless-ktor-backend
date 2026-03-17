package at.eventful.messless.services.auth.commands

import kotlinx.serialization.Serializable

@Serializable
data class CreateAuthBasicCmd(
    val email: String,
    val password: String,
)