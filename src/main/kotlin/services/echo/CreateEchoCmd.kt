package at.eventful.messless.services.echo

import kotlinx.serialization.Serializable

@Serializable
data class CreateEchoCmd(var message: String)