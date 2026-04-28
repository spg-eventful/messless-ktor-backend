package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.TechnicalLogEntryEntity
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// TODO: Use EquipmentDao instead of Int in attachedTo
@Serializable
@OptIn(ExperimentalTime::class)
data class TechnicalLogEntryDao(
    val id: Int,
    val isCheckIn: Boolean,
    val attachedTo: EquipmentDao?,
    val byUser: UserDao?,
    val loggable: LoggableDao?,
    val createdAt: Instant,
    val longitude: Double? = null,
    val latitude: Double? = null,
) {
    companion object : ConvertibleDao<TechnicalLogEntryEntity, TechnicalLogEntryDao> {
        override fun from(entity: TechnicalLogEntryEntity?): TechnicalLogEntryDao? = entity?.let {
            TechnicalLogEntryDao(
                id = entity.id.value,
                isCheckIn = entity.isCheckIn,
                attachedTo = EquipmentDao.from(entity.attachedTo),
                byUser = UserDao.from(entity.byUser),
                loggable = LoggableDao.from(entity.loggable),
                createdAt = entity.createdAt,
                longitude = entity.location.x,
                latitude = entity.location.y,
            )
        }

        fun fake(id: Int) = TechnicalLogEntryDao(
            id = id,
            isCheckIn = false,
            attachedTo = EquipmentDao.fake(1),
            byUser = UserDao.fake(1),
            loggable = LoggableDao.fake(1),
            createdAt = Instant.DISTANT_PAST,
            longitude = 0.0,
            latitude = 0.0,
        )
    }
}