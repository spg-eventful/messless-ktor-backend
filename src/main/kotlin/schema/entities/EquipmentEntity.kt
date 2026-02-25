package at.eventful.messless.schema.entities

import at.eventful.messless.schema.tables.EquipmentTable
import at.eventful.messless.schema.utils.LoggableEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class EquipmentEntity(id: EntityID<Int>): LoggableEntity(id) {
    companion object : IntEntityClass<EquipmentEntity>(EquipmentTable)
    override var createdAt by EquipmentTable.createdAt
    override var updatedAt by EquipmentTable.updatedAt
    override var deletedAt by EquipmentTable.deletedAt
    override var label by EquipmentTable.label
    override var location by EquipmentTable.location

    var belongsTo by WarehouseEntity referencedOn EquipmentTable.belongsTo
    var storage by EquipmentStorageEntity optionalReferencedOn EquipmentTable.storage
}