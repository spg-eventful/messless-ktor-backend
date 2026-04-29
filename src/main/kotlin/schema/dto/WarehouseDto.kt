package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.LoggableDao
import at.eventful.messless.schema.dao.WarehouseDao
import kotlinx.serialization.Serializable

@Serializable
data class WarehouseDto(
    val id: Int,
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val company: Int
){
    companion object{
        fun from(warehouse: WarehouseDao, loggable: LoggableDao): WarehouseDto = WarehouseDto(
            id = warehouse.id,
            label = loggable.label,
            latitude = loggable.latitude,
            longitude = loggable.longitude,
            company = warehouse.company!!.id
        )
    }
}

