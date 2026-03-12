package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.EquipmentStorageEntity
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentStorageDao(val id: Int) {
    companion object : ConvertibleDao<EquipmentStorageEntity, EquipmentStorageDao> {
        override fun from(entity: EquipmentStorageEntity?): EquipmentStorageDao? {
            TODO("Not yet implemented")
        }

    }
}