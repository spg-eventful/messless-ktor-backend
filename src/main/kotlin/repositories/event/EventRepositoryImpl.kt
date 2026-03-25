package at.eventful.messless.repositories.event

import at.eventful.messless.repositories.event.commands.CreateEventCmd
import at.eventful.messless.repositories.event.commands.UpdateEventCmd
import at.eventful.messless.schema.dao.EventDao
import at.eventful.messless.schema.entities.EventEntity
import at.eventful.messless.schema.tables.EventTable
import net.postgis.jdbc.geometry.Point
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class EventRepositoryImpl : EventRepository {
    override fun addEvent(event: CreateEventCmd): EventDao = transaction {
        EventDao.from(EventEntity.new {
            label = event.label
            location = Point(event.longitude, event.latitude)
        })!!
    }

    @OptIn(ExperimentalTime::class)
    override fun allEvents(): List<EventDao> = transaction {
        EventEntity.find { (EventTable.deletedAt eq null) }.toList()
            .map { EventDao::from } as List<EventDao>
    }

    @OptIn(ExperimentalTime::class)
    override fun eventById(id: Int): EventDao? = transaction {
        val event = EventEntity.findById(id)
        return@transaction if (event?.deletedAt == null) EventDao.from(event) else null
    }

    override fun updateEvent(id: Int, event: UpdateEventCmd): EventDao? = transaction {
        EventDao.from(EventEntity.findByIdAndUpdate(id) {
            it.label = event.label
            it.location = Point(event.longitude, event.latitude)
        })
    }

    @OptIn(ExperimentalTime::class)
    override fun removeEvent(id: Int): EventDao? = transaction {
        EventDao.from(EventEntity.findByIdAndUpdate(id) {
            it.deletedAt = Clock.System.now()
        })
    }
}