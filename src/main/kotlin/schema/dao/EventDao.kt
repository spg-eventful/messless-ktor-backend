package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.EventEntity
import at.eventful.messless.schema.entities.LoggableEntity
import kotlinx.serialization.Serializable

@Serializable
data class EventDao(
    val id: Int,
    val loggable: LoggableDao?,
) {
    companion object : ConvertibleDao<EventEntity, EventDao> {
        override fun from(entity: EventEntity?, loggable: LoggableEntity?): EventDao? = entity?.let {
            if (loggable == null) return@let null
            EventDao(
                id = entity.id.value,
                loggable = LoggableDao.from(entity.loggable),
            )
        }

        fun fake(id: Int) = EventDao(
            id = id,
            loggable = LoggableDao.fake(1),
        )
    }
}