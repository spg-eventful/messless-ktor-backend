package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.LoggableEntity
import at.eventful.messless.schema.entities.WarehouseEntity
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
        override fun from(entity: WarehouseEntity?, loggable: LoggableEntity?): WarehouseDao? = entity?.let {
            if (loggable == null) return@let null
            WarehouseDao(
                id = entity.id.value,
                label = loggable.label,
                latitude = loggable.location.x,
                longitude = loggable.location.y,
                company = CompanyDao.from(entity.company)
            )
        }

        fun fake(id: Int, label: String = "fake warehouse") = WarehouseDao(
            id = id,
            label = label,
            latitude = 0.0,
            longitude = 0.0,
            company = CompanyDao.fake(1)
        )
    }

}