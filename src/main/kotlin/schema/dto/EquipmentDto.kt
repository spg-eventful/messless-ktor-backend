package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.EquipmentDao
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.postgis.jdbc.geometry.Point

@Serializable
data class EquipmentDto(
    val id: Int,
    val label: String,
    @Contextual
    val location: Point
) {
    companion object {
        fun from(equipment: EquipmentDao): EquipmentDto = EquipmentDto(
            id = equipment.id,
            label = equipment.label,
            location = equipment.location
        )
    }
}