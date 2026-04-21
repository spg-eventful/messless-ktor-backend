package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.EquipmentEntity
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDao(
    val id: Int,
    val label: String,
    val belongsToWarehouse: Int,
    val storage: Int?,
    val isStorage: Boolean
) {
    companion object : ConvertibleDao<EquipmentEntity, EquipmentDao> {
        override fun from(entity: EquipmentEntity?): EquipmentDao? = entity?.let {
            EquipmentDao(
                id = entity.id.value,
                label = entity.label,
                belongsToWarehouse = entity.belongsTo.id.value,
                storage = entity.isStorage?.id?.value,
                isStorage = entity.isStorage?.id?.value != null
            )
        }

        fun fake(id: Int) = EquipmentDao(
            id = id,
            label = "Fake equipment",
            belongsToWarehouse = 1,
            storage = 1,
            isStorage = false
        )
    }
}