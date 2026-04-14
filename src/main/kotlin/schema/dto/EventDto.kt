package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.EventDao
import at.eventful.messless.schema.dao.LoggableDao
import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: Int,
    val label: String,
    val longitude: Double,
    val latitude: Double,
) {
    companion object {
        fun from(event: EventDao, loggable: LoggableDao): EventDto = EventDto(
            id = event.id,
            label = loggable.label,
            longitude = loggable.longitude,
            latitude = loggable.latitude,
        )
    }
}