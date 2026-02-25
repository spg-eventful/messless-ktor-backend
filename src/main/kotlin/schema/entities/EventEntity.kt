package at.eventful.messless.schema.entities

import at.eventful.messless.schema.tables.EventTable
import at.eventful.messless.schema.utils.LoggableEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class EventEntity(id: EntityID<Int>): LoggableEntity(id) {
    companion object : IntEntityClass<EventEntity>(EventTable)
    override var createdAt by EventTable.createdAt
    override var updatedAt by EventTable.updatedAt
    override var deletedAt by EventTable.deletedAt
    override var label by EventTable.label
    override var location by EventTable.location
}