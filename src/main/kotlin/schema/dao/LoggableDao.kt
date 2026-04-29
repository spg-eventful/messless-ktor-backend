package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.LoggableEntity
import at.eventful.messless.schema.utils.LoggableType
import kotlinx.serialization.Serializable

@Serializable
data class LoggableDao(
    val id: Int,
    val label: String,
    val longitude: Double,
    val latitude: Double,
    val loggableType: LoggableType,
) {
    companion object : ConvertibleDao<LoggableEntity, LoggableDao> {
        override fun from(entity: LoggableEntity?): LoggableDao? = entity?.let {
            LoggableDao(
                id = entity.id.value,
                label = entity.label,
                longitude = entity.location.x,
                latitude = entity.location.y,
                loggableType = entity.loggableType,
            )
        }

        fun fake(id: Int) = LoggableDao(
            id = id,
            label = "Fake Loggable",
            longitude = 0.0,
            latitude = 0.0,
            loggableType = LoggableType.Event
        )
    }
}