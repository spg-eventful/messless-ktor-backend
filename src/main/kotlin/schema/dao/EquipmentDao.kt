package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.EquipmentEntity
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.postgis.jdbc.geometry.Point

@Serializable
data class EquipmentDao(
    val id: Int,
    val label: String,
    @Contextual
    val location: Point,
    val belongsTo: WarehouseDao?,
    val storage: EquipmentStorageDao?,
) {
    companion object : ConvertibleDao<EquipmentEntity, EquipmentDao> {
        override fun from(entity: EquipmentEntity?): EquipmentDao? = entity?.let {
            EquipmentDao(
                id = entity.id.value,
                label = entity.label,
                location = entity.location,
                belongsTo = WarehouseDao.from(entity.belongsTo),
                storage = if (entity.storage != null) EquipmentStorageDao.from(entity.storage!!) else null,
            )
        }

        fun fake(id: Int) = EquipmentDao(
            id = id,
            label = "Fake equipment",
            location = Point(0.0, 0.0),
            belongsTo = null, //TODO
            storage = null, //TODO
        )
    }
}