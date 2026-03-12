package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.CompanyDao
import at.eventful.messless.schema.dao.WarehouseDao
import kotlinx.serialization.Serializable

@Serializable
data class WarehouseDto(
    val id: Int,
    val label: String,
    val latitude: Double,
    val longitude: Double,
){
    companion object{
        fun from(warehouse: WarehouseDao): WarehouseDto = WarehouseDto(
            id = warehouse.id,
            label = warehouse.label,
            latitude = warehouse.latitude,
            longitude = warehouse.longitude,
        )

    }
}

