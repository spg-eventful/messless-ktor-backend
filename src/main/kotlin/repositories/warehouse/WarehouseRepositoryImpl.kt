package at.eventful.messless.repositories.warehouse

import at.eventful.messless.repositories.loggable.LoggableRepositoryImpl
import at.eventful.messless.repositories.loggable.command.CreateLoggableCmd
import at.eventful.messless.repositories.loggable.command.UpdateLoggableCmd
import at.eventful.messless.schema.dao.WarehouseDao
import at.eventful.messless.schema.entities.CompanyEntity
import at.eventful.messless.schema.entities.LoggableEntity
import at.eventful.messless.schema.entities.WarehouseEntity
import at.eventful.messless.schema.tables.WarehouseTable
import at.eventful.messless.schema.utils.LoggableType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import repositories.warehouse.command.CreateWarehouseCmd
import repositories.warehouse.command.UpdateWarehouseCmd
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class WarehouseRepositoryImpl : WarehouseRepository {
    val loggableRepository = LoggableRepositoryImpl()

    override fun addWarehouse(createWarehouseCommand: CreateWarehouseCmd): WarehouseDao = transaction {
        WarehouseDao.from(
            WarehouseEntity.new {
                loggable = LoggableEntity.findById(
                    loggableRepository.addLoggable(
                        CreateLoggableCmd(
                            createWarehouseCommand.label,
                            createWarehouseCommand.longitude,
                            createWarehouseCommand.latitude,
                            LoggableType.Warehouse
                        )
                    ).id
                ) ?: throw Error("Loggable not found")
                company = CompanyEntity.findById(createWarehouseCommand.companyId) ?: throw Error("Company not found")
            }
        )!!
    }

    @OptIn(ExperimentalTime::class)
    override fun allWarehouses(): List<WarehouseDao> = transaction {
        WarehouseEntity.find { WarehouseTable.deletedAt.isNull() }.toList()
            .map(WarehouseDao::from) as List<WarehouseDao>
    }

    @OptIn(ExperimentalTime::class)
    override fun warehouseById(id: Int): WarehouseDao? = transaction {
        val warehouse = WarehouseEntity.findById(id)
        return@transaction if (warehouse?.deletedAt == null) WarehouseDao.from(warehouse) else null
    }

    @OptIn(ExperimentalTime::class)
    override fun updateWarehouse(id: Int, updateWarehouseCmd: UpdateWarehouseCmd): WarehouseDao = transaction {
        WarehouseDao.from(WarehouseEntity.findByIdAndUpdate(updateWarehouseCmd.`$id`) {
            loggableRepository.updateLoggable(
                it.loggable.id.value,
                UpdateLoggableCmd(
                    it.loggable.id.value,
                    updateWarehouseCmd.label,
                    updateWarehouseCmd.longitude,
                    updateWarehouseCmd.latitude,
                    LoggableType.Warehouse
                )
            )
        })!!
    }

    @OptIn(ExperimentalTime::class)
    override fun removeWarehouse(id: Int): WarehouseDao? = transaction {
        WarehouseDao.from(WarehouseEntity.findSingleByAndUpdate(WarehouseTable.id eq id and WarehouseTable.deletedAt.isNull()) {
            loggableRepository.removeLoggable(it.loggable.id.value)
            it.deletedAt = Clock.System.now()
        })
    }
}