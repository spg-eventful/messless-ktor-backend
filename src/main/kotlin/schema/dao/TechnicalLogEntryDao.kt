package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.TechnicalLogEntryEntity
import kotlinx.serialization.Serializable

// TODO: Use EquipmentDao instead of Int in attachedTo
@Serializable
data class TechnicalLogEntryDao(
    val id: Int,
    val isCheckIn: Boolean,
    val attachedTo: EquipmentDao?,
    val byUser: UserDao?,
    val loggable: Int,
) {
    companion object : ConvertibleDao<TechnicalLogEntryEntity, TechnicalLogEntryDao> {
        override fun from(entity: TechnicalLogEntryEntity?): TechnicalLogEntryDao? = entity?.let {
            TechnicalLogEntryDao(
                id = entity.id.value,
                isCheckIn = entity.isCheckIn,
                attachedTo = EquipmentDao.from(entity.attachedTo),
                byUser = UserDao.from(entity.byUser),
                loggable = entity.loggable,
            )
        }

        fun fake(id: Int) = TechnicalLogEntryDao(
            id = id,
            isCheckIn = false,
            attachedTo = EquipmentDao.fake(1),
            byUser = UserDao.fake(1),
            loggable = 1
        )
    }
}