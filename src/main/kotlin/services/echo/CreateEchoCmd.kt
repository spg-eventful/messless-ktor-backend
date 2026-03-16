package at.eventful.messless.services.echo

import kotlinx.serialization.Serializable

@Serializable
data class CreateEchoCmd(val message: String, val checkAuthentication: Boolean = false)