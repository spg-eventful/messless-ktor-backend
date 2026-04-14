package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.EquipmentEntity
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDao(
    val id: Int,
    val label: String,
    val belongsToWarehouse: Int,
    val equipmentStorage: Int?,
) {
    companion object : ConvertibleDao<EquipmentEntity, EquipmentDao> {
        override fun from(entity: EquipmentEntity?): EquipmentDao? = entity?.let {
            EquipmentDao(
                id = entity.id.value,
                label = entity.label,
                belongsToWarehouse = entity.belongsTo.id.value,
                equipmentStorage = entity.isStorage?.id?.value,
            )
        }

        fun fake(id: Int) = EquipmentDao(
            id = id,
            label = "Fake equipment",
            belongsToWarehouse = 1,
            equipmentStorage = 1,
        )
    }
}