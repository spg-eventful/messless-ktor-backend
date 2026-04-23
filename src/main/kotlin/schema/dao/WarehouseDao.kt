package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.WarehouseEntity
import kotlinx.serialization.Serializable

@Serializable
data class WarehouseDao (
    val id: Int,
    val company: CompanyDao?,
    val loggable: LoggableDao?,
){
    companion object : ConvertibleDao<WarehouseEntity, WarehouseDao>{
        override fun from(entity: WarehouseEntity?): WarehouseDao? = entity?.let {
            WarehouseDao(
                id = entity.id.value,
                company = CompanyDao.from(entity.company),
                loggable = LoggableDao.from(entity.loggable),
            )
        }

        fun fake(id: Int, label: String = "fake warehouse") = WarehouseDao(
            id = id,
            company = CompanyDao.fake(1),
            loggable = LoggableDao.fake(1),
        )
    }

}