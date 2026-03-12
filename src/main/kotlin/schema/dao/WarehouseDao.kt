package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.WarehouseEntity
import at.eventful.messless.schema.utils.Point
import kotlinx.serialization.Serializable

@Serializable
data class WarehouseDao (
    val id: Int,
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val company: CompanyDao?
){
    companion object : ConvertibleDao<WarehouseEntity, WarehouseDao>{
        override fun from(entity: WarehouseEntity?): WarehouseDao? = entity?.let {
            WarehouseDao(
                id = entity.id.value,
                label = entity.label,
                latitude = entity.location.x,
                longitude = entity.location.y,
                company = CompanyDao.from(entity.company)
            )
        }

        fun fake(id: Int, label: String = "fake warehouse") = WarehouseDao(
            id = id,
            label = label,
            latitude = 0.0,
            longitude = 0.0,
            company = null
        )
    }

}