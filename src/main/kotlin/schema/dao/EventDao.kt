package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.EventEntity
import at.eventful.messless.schema.entities.LoggableEntity
import kotlinx.serialization.Serializable

@Serializable
data class EventDao(
    val id: Int,
    val label: String,
    val longitude: Double,
    val latitude: Double,
) {
    companion object : ConvertibleDao<EventEntity, EventDao> {
        override fun from(entity: EventEntity?, loggable: LoggableEntity?): EventDao? = entity?.let {
            if (loggable == null) return@let null
            EventDao(
                id = entity.id.value,
                label = loggable.label,
                latitude = loggable.location.x,
                longitude = loggable.location.y,
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