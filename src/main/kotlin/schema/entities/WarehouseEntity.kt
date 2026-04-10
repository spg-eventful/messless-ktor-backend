package at.eventful.messless.schema.entities

import at.eventful.messless.schema.tables.WarehouseTable
import at.eventful.messless.schema.utils.BaseEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class WarehouseEntity(id: EntityID<Int>) : BaseEntity(id) {
    companion object : IntEntityClass<WarehouseEntity>(WarehouseTable)

    override var createdAt by WarehouseTable.createdAt
    override var updatedAt by WarehouseTable.updatedAt
    override var deletedAt by WarehouseTable.deletedAt

    var company by CompanyEntity referencedOn WarehouseTable.companyId
    var loggable by LoggableEntity referencedOn WarehouseTable.loggable
}