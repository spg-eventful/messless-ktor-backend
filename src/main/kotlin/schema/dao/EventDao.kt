package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.EventEntity
import kotlinx.serialization.Serializable

@Serializable
data class EventDao(
    val id: Int,
    val label: String,
    val longitude: Double,
    val latitude: Double,
) {
    companion object : ConvertibleDao<EventEntity, EventDao> {
        override fun from(entity: EventEntity?): EventDao? = entity?.let {
            EventDao(
                id = entity.id.value,
                label = entity.label,
                latitude = entity.location.x,
                longitude = entity.location.y,
            )
        }

        fun fake(id: Int) = EventDao(
            id = id,
            label = "Fake event",
            latitude = 0.0,
            longitude = 0.0,
        )
    }
}