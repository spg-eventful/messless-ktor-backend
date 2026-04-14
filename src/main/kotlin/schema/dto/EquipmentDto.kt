package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.EquipmentDao
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDto(
    val id: Int,
    val label: String,
    val belongsToWarehouse: Int,
) {
    companion object {
        fun from(equipment: EquipmentDao): EquipmentDto = EquipmentDto(
            id = equipment.id,
            label = equipment.label,
            belongsToWarehouse = equipment.belongsToWarehouse,
        )
    }
}