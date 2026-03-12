package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.EquipmentEntity
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDao(
    val id: Int,
    val label: String,
    val longitude: Double,
    val latitude: Double,
    val belongsToWarehouse: Int,
    val equipmentStorage: Int?,
) {
    companion object : ConvertibleDao<EquipmentEntity, EquipmentDao> {
        override fun from(entity: EquipmentEntity?): EquipmentDao? = entity?.let {
            EquipmentDao(
                id = entity.id.value,
                label = entity.label,
                latitude = entity.location.x,
                longitude = entity.location.y,
                belongsToWarehouse = entity.belongsTo.id.value,
                equipmentStorage = entity.storage?.id?.value,
            )
        }

        fun fake(id: Int) = EquipmentDao(
            id = id,
            label = "Fake equipment",
            latitude = 0.0,
            longitude = 0.0,
            belongsToWarehouse = 1,
            equipmentStorage = 1,
        )
    }
}