package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.EventDao
import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: Int,
    val label: String,
    val longitude: Double,
    val latitude: Double,
) {
    companion object {
        fun from(event: EventDao): EventDto = EventDto(
            id = event.id,
            label = event.label,
            longitude = event.longitude,
            latitude = event.latitude,
        )
    }
}