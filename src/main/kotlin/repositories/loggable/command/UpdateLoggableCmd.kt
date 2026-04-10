package at.eventful.messless.repositories.loggable.command

import at.eventful.messless.schema.utils.LoggableType
import kotlinx.serialization.Serializable

@Serializable
data class UpdateLoggableCmd(
    val `$id`: Int,
    var label: String,
    var longitude: Double,
    var latitude: Double,
    var loggableType: LoggableType,
)