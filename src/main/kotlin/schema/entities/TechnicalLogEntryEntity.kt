package at.eventful.messless.schema.entities

import at.eventful.messless.schema.tables.TechnicalLogEntryTable
import at.eventful.messless.schema.utils.BaseEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TechnicalLogEntryEntity(id: EntityID<Int>) : BaseEntity(id) {
    companion object : IntEntityClass<TechnicalLogEntryEntity>(TechnicalLogEntryTable)

    override var createdAt by TechnicalLogEntryTable.createdAt
    override var updatedAt by TechnicalLogEntryTable.updatedAt
    override var deletedAt by TechnicalLogEntryTable.deletedAt

    var isCheckIn by TechnicalLogEntryTable.isCheckIn
    // the Equipment the log entry is attached to
    var attachedTo by TechnicalLogEntryTable.attachedTo
    var byUser by UserEntity referencedOn TechnicalLogEntryTable.byUser

    var loggable by TechnicalLogEntryTable.loggable
}