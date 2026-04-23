package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.EquipmentStorageEntity
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentStorageDao(
    val id: Int,
    val loggable: LoggableDao?,
) {
    companion object : ConvertibleDao<EquipmentStorageEntity, EquipmentStorageDao> {
        override fun from(entity: EquipmentStorageEntity?): EquipmentStorageDao? =
            entity?.let {
                EquipmentStorageDao(
                    id = entity.id.value,
                    loggable = LoggableDao.from(entity.loggable),
                )
        }

        fun fake(id: Int) = EquipmentStorageDao(
            id = id,
            loggable = LoggableDao.fake(1),
        )
    }
}