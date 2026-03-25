package at.eventful.messless.repositories.event.commands

import kotlinx.serialization.Serializable

@Serializable
data class UpdateEventCmd(
    val `$id`: Int,
    var label: String,
    var longitude: Double,
    var latitude: Double,
)