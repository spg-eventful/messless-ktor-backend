package at.eventful.messless.repositories.loggable

import at.eventful.messless.repositories.loggable.command.CreateLoggableCmd
import at.eventful.messless.repositories.loggable.command.UpdateLoggableCmd
import at.eventful.messless.schema.dao.LoggableDao

interface LoggableRepository {
    fun addLoggable(loggable: CreateLoggableCmd): LoggableDao
    fun allLoggables(): List<LoggableDao>
    fun loggableById(id: Int): LoggableDao?
    fun updateLoggable(id: Int, loggable: UpdateLoggableCmd): LoggableDao?
    fun removeLoggable(id: Int): LoggableDao?
}