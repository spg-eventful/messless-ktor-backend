package at.eventful.messless.repositories.event

import at.eventful.messless.repositories.event.commands.CreateEventCmd
import at.eventful.messless.repositories.event.commands.UpdateEventCmd
import at.eventful.messless.repositories.loggable.LoggableRepositoryImpl
import at.eventful.messless.repositories.loggable.command.CreateLoggableCmd
import at.eventful.messless.repositories.loggable.command.UpdateLoggableCmd
import at.eventful.messless.schema.dao.EventDao
import at.eventful.messless.schema.entities.EventEntity
import at.eventful.messless.schema.entities.LoggableEntity
import at.eventful.messless.schema.tables.EventTable
import at.eventful.messless.schema.utils.LoggableType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class EventRepositoryImpl : EventRepository {
    val loggableRepository = LoggableRepositoryImpl()

    override fun addEvent(event: CreateEventCmd): EventDao = transaction {
        EventDao.from(EventEntity.new {
            loggable = LoggableEntity.findById(
                loggableRepository.addLoggable(
                    CreateLoggableCmd(
                        event.label,
                        event.longitude,
                        event.latitude,
                        LoggableType.Event
                    )
                ).id
            ) ?: throw Error("Loggable not found")
        })!!
    }

    @OptIn(ExperimentalTime::class)
    override fun allEvents(): List<EventDao> = transaction {
        val mapper: (EventEntity?) -> EventDao? = EventDao::from
        EventEntity.find { (EventTable.deletedAt eq null) }.toList()
            .map { mapper } as List<EventDao>
    }

    @OptIn(ExperimentalTime::class)
    override fun eventById(id: Int): EventDao? = transaction {
        val event = EventEntity.findById(id)
        return@transaction if (event?.deletedAt == null) EventDao.from(event) else null
    }

    override fun updateEvent(id: Int, event: UpdateEventCmd): EventDao? = transaction {
        EventDao.from(EventEntity.findByIdAndUpdate(id) {
            loggableRepository.updateLoggable(
                it.loggable!!.id.value,
                UpdateLoggableCmd(
                    it.loggable!!.id.value,
                    event.label,
                    event.longitude,
                    event.latitude,
                    LoggableType.Event
                )
            )
        })
    }

    @OptIn(ExperimentalTime::class)
    override fun removeEvent(id: Int): EventDao? = transaction {
        EventDao.from(EventEntity.findByIdAndUpdate(id) {
            loggableRepository.removeLoggable(it.loggable!!.id.value)
            it.deletedAt = Clock.System.now()
        })
    }
}