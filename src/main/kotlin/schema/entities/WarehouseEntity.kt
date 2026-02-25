package at.eventful.messless.schema.entities

import at.eventful.messless.schema.tables.WarehouseTable
import at.eventful.messless.schema.utils.LoggableEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class WarehouseEntity(id: EntityID<Int>): LoggableEntity(id) {
    companion object : IntEntityClass<WarehouseEntity>(WarehouseTable)
    override var createdAt by WarehouseTable.createdAt
    override var updatedAt by WarehouseTable.updatedAt
    override var deletedAt by WarehouseTable.deletedAt
    override var label by WarehouseTable.label
    override var location by WarehouseTable.location

    var company by CompanyEntity referencedOn WarehouseTable.companyId
}