package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.EquipmentDao
import at.eventful.messless.schema.dao.LoggableDao
import at.eventful.messless.schema.utils.LoggableType
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDto(
    val id: Int,
    val label: String,
    val belongsToWarehouse: Int,
    val storage: Int?,
    val longitude: Double?,
    val latitude: Double?,
    val loggableType: LoggableType?,
) {
    companion object {
        fun from(equipment: EquipmentDao, loggable: LoggableDao?, longitude: Double?, latitude: Double?): EquipmentDto = EquipmentDto(
            id = equipment.id,
            label = equipment.label,
            belongsToWarehouse = equipment.belongsToWarehouse,
            storage = equipment.storage,
            longitude = loggable?.longitude ?: longitude,
            latitude = loggable?.latitude ?: latitude,
            loggableType = loggable?.loggableType,
        )
    }
}