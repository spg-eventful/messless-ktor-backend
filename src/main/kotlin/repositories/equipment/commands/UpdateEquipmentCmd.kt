package at.eventful.messless.repositories.equipment.commands

import at.eventful.messless.schema.entities.EquipmentStorageEntity
import at.eventful.messless.schema.entities.WarehouseEntity
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.postgis.jdbc.geometry.Point

@Serializable
data class UpdateEquipmentCmd(
    val `$id`: Int,
    var label: String,
    @Contextual
    var location: Point,
    var belongsTo: WarehouseEntity,
    var storage: EquipmentStorageEntity?,
)