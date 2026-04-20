package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.EquipmentStorageEntity
import at.eventful.messless.schema.entities.LoggableEntity
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentStorageDao(
    val id: Int,
    val loggable: LoggableDao?,
) {
    companion object : ConvertibleDao<EquipmentStorageEntity, EquipmentStorageDao> {
        override fun from(entity: EquipmentStorageEntity?, loggable: LoggableEntity?): EquipmentStorageDao? =
            entity?.let {
                if (loggable == null) return@let null
                EquipmentStorageDao(
                    id = entity.id.value,
                    loggable = LoggableDao.from(entity.loggable),
                )
        }
    }
}