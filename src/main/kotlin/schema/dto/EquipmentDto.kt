package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.EquipmentDao
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDto(
    val id: Int,
    val label: String,
    val longitude: Double,
    val latitude: Double,
    val belongsToWarehouse: Int,
) {
    companion object {
        fun from(equipment: EquipmentDao): EquipmentDto = EquipmentDto(
            id = equipment.id,
            label = equipment.label,
            longitude = equipment.longitude,
            latitude = equipment.latitude,
            belongsToWarehouse = equipment.belongsToWarehouse,
        )
    }
}