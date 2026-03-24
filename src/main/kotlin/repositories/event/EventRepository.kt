package at.eventful.messless.repositories.event

import at.eventful.messless.repositories.event.commands.CreateEventCmd
import at.eventful.messless.repositories.event.commands.UpdateEventCmd
import at.eventful.messless.schema.dao.EventDao

interface EventRepository {
    fun addEvent(event: CreateEventCmd): EventDao
    fun allEvents(): List<EventDao>
    fun eventById(id: Int): EventDao?
    fun updateEvent(id: Int, event: UpdateEventCmd): EventDao?
    fun removeEvent(id: Int): EventDao?
}