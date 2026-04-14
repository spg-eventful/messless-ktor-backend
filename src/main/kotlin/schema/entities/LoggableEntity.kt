package at.eventful.messless.schema.entities

import at.eventful.messless.schema.tables.LoggableTable
import at.eventful.messless.schema.utils.BaseEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class LoggableEntity(id: EntityID<Int>) : BaseEntity(id) {
    companion object : IntEntityClass<LoggableEntity>(LoggableTable)

    override var createdAt by LoggableTable.createdAt
    override var updatedAt by LoggableTable.updatedAt
    override var deletedAt by LoggableTable.deletedAt

    var label by LoggableTable.label
    var location by LoggableTable.location
    var loggableType by LoggableTable.loggable_type

    var company by CompanyEntity referencedOn LoggableTable.companyId
}