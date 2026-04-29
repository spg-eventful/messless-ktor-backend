package at.eventful.messless.repositories.loggable.command

import at.eventful.messless.schema.utils.LoggableType
import kotlinx.serialization.Serializable

@Serializable
data class CreateLoggableCmd(
    var label: String,
    var latitude: Double,
    var longitude: Double,
    var loggableType: LoggableType,
    var companyId: Int,
)