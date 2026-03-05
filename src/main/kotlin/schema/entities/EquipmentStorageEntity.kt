package at.eventful.messless.schema.entities

import at.eventful.messless.schema.tables.EquipmentStorageTable
import at.eventful.messless.schema.utils.LoggableEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class EquipmentStorageEntity(id: EntityID<Int>) : LoggableEntity(id) {
    companion object : IntEntityClass<EquipmentStorageEntity>(EquipmentStorageTable)

    override var createdAt by EquipmentStorageTable.createdAt
    override var updatedAt by EquipmentStorageTable.updatedAt
    override var deletedAt by EquipmentStorageTable.deletedAt
    override var label by EquipmentStorageTable.label
    override var location by EquipmentStorageTable.location
}