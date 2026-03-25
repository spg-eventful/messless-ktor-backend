package at.eventful.messless.repositories.event.commands

import kotlinx.serialization.Serializable

@Serializable
data class CreateEventCmd(
    var label: String,
    var longitude: Double,
    var latitude: Double,
)