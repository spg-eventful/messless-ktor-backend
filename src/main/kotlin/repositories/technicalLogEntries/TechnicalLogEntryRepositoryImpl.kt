package at.eventful.messless.repositories.technicalLogEntries

import at.eventful.messless.repositories.technicalLogEntries.commands.CreateTechnicalLogEntryCmd
import at.eventful.messless.schema.dao.TechnicalLogEntryDao
import at.eventful.messless.schema.entities.EquipmentEntity
import at.eventful.messless.schema.entities.TechnicalLogEntryEntity
import at.eventful.messless.schema.entities.UserEntity
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TechnicalLogEntryRepositoryImpl : TechnicalLogEntryRepository {
    override fun addTechnicalLogEntry(createTechnicalLogEntryCommand: CreateTechnicalLogEntryCmd): TechnicalLogEntryDao =
        transaction {
            TechnicalLogEntryDao.from(
                TechnicalLogEntryEntity.new {
                    isCheckIn = createTechnicalLogEntryCommand.isCheckIn
                    attachedTo = EquipmentEntity.findById(createTechnicalLogEntryCommand.attachedTo)!!
                    byUser = UserEntity.findById(createTechnicalLogEntryCommand.byUser)!!
                    loggable = createTechnicalLogEntryCommand.loggable
                }
            )!!
        }

    override fun allTechnicalLogEntries(): List<TechnicalLogEntryDao> = transaction {
        TechnicalLogEntryEntity.all().map(TechnicalLogEntryDao::from) as List<TechnicalLogEntryDao>
    }

    @OptIn(ExperimentalTime::class)
    override fun removeTechnicalLogEntry(id: Int): TechnicalLogEntryDao? = transaction {
        TechnicalLogEntryDao.from(TechnicalLogEntryEntity.findByIdAndUpdate(id) {
            it.deletedAt = Clock.System.now()
        })
    }
}