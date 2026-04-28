package at.eventful.messless.repositories.technicalLogEntries

import at.eventful.messless.repositories.technicalLogEntries.commands.CreateTechnicalLogEntryCmd
import at.eventful.messless.schema.dao.TechnicalLogEntryDao
import at.eventful.messless.schema.entities.EquipmentEntity
import at.eventful.messless.schema.entities.LoggableEntity
import at.eventful.messless.schema.entities.TechnicalLogEntryEntity
import at.eventful.messless.schema.entities.UserEntity
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import net.postgis.jdbc.geometry.Point
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TechnicalLogEntryRepositoryImpl : TechnicalLogEntryRepository {
    override fun addTechnicalLogEntry(createTechnicalLogEntryCommand: CreateTechnicalLogEntryCmd, userId: Int): TechnicalLogEntryDao =
        transaction {
            TechnicalLogEntryDao.from(
                TechnicalLogEntryEntity.new {
                    isCheckIn = createTechnicalLogEntryCommand.isCheckIn
                    attachedTo = EquipmentEntity.findById(createTechnicalLogEntryCommand.attachedTo)!!
                    byUser = UserEntity.findById(userId)!!
                    loggable = LoggableEntity.findById(createTechnicalLogEntryCommand.loggable)!!
                    location = Point(createTechnicalLogEntryCommand.longitude, createTechnicalLogEntryCommand.latitude)
                }
            )!!
        }

    override fun allTechnicalLogEntries(): List<TechnicalLogEntryDao> = transaction {
        TechnicalLogEntryEntity.all().mapNotNull { TechnicalLogEntryDao.from(it) }
    }

    @OptIn(ExperimentalTime::class)
    override fun removeTechnicalLogEntry(id: Int): TechnicalLogEntryDao? = transaction {
        TechnicalLogEntryDao.from(TechnicalLogEntryEntity.findByIdAndUpdate(id) {
            it.deletedAt = Clock.System.now()
        })
    }

    @OptIn(ExperimentalTime::class)
    override fun technicalLogEntryById(id: Int): TechnicalLogEntryDao? = transaction {
        val technicalLog = TechnicalLogEntryEntity.findById(id)
        return@transaction if (technicalLog?.deletedAt == null) TechnicalLogEntryDao.from(technicalLog) else null
    }
}