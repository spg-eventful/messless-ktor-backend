package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.WarehouseEntity
import kotlinx.serialization.Serializable

@Serializable
data class WarehouseDao(val id: Int) {
    companion object : ConvertibleDao<WarehouseEntity, WarehouseDao> {
        override fun from(entity: WarehouseEntity?): WarehouseDao? {
            TODO("Not yet implemented")
        }

    }
}