package at.eventful.messless.repositories.loggable

import at.eventful.messless.repositories.loggable.command.CreateLoggableCmd
import at.eventful.messless.repositories.loggable.command.UpdateLoggableCmd
import at.eventful.messless.schema.dao.LoggableDao
import at.eventful.messless.schema.entities.CompanyEntity
import at.eventful.messless.schema.entities.LoggableEntity
import at.eventful.messless.schema.tables.LoggableTable
import net.postgis.jdbc.geometry.Point
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class LoggableRepositoryImpl : LoggableRepository {
    override fun addLoggable(loggable: CreateLoggableCmd): LoggableDao = transaction {
        LoggableDao.from(LoggableEntity.new {
            label = loggable.label
            location = Point(loggable.longitude, loggable.latitude)
            loggableType = loggable.loggableType
            company = CompanyEntity.findById(loggable.companyId) ?: throw Error("Company not found")
        })!!
    }

    @OptIn(ExperimentalTime::class)
    override fun allLoggables(): List<LoggableDao> = transaction {
        LoggableEntity.find { (LoggableTable.deletedAt eq null) }.toList()
            .mapNotNull { LoggableDao.from(it) }
    }

    @OptIn(ExperimentalTime::class)
    override fun loggableById(id: Int): LoggableDao? = transaction {
        val loggable = LoggableEntity.findById(id)
        return@transaction if (loggable?.deletedAt == null) LoggableDao.from(loggable) else null
    }

    override fun updateLoggable(id: Int, loggable: UpdateLoggableCmd): LoggableDao? = transaction {
        LoggableDao.from(LoggableEntity.findByIdAndUpdate(id) {
            it.label = loggable.label
            it.location = Point(loggable.longitude, loggable.latitude)
            it.loggableType = loggable.loggableType
            it.company = CompanyEntity.findById(loggable.companyId) ?: throw Error("Company not found")
        })
    }

    @OptIn(ExperimentalTime::class)
    override fun removeLoggable(id: Int): LoggableDao? = transaction {
        LoggableDao.from(LoggableEntity.findByIdAndUpdate(id) {
            it.deletedAt = Clock.System.now()
        })
    }
}