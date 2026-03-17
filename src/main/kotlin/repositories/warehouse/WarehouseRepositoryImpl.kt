package at.eventful.messless.repositories.warehouse

import at.eventful.messless.schema.dao.WarehouseDao
import at.eventful.messless.schema.entities.WarehouseEntity
import at.eventful.messless.schema.tables.WarehouseTable
import net.postgis.jdbc.geometry.Point
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import repositories.warehouse.command.CreateWarehouseCmd
import repositories.warehouse.command.UpdateWarehouseCmd
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class WarehouseRepositoryImpl : WarehouseRepository {
    override fun addWarehouse(createWarehouseCommand: CreateWarehouseCmd): WarehouseDao = transaction {
        WarehouseDao.from(
            WarehouseEntity.new {
                label = createWarehouseCommand.label
                location = Point(createWarehouseCommand.latitude, createWarehouseCommand.longitude)
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
    override fun updateWarehouse(id: Int, updateWarehouseCmd: UpdateWarehouseCmd): WarehouseDao? = transaction {
        WarehouseDao.from(WarehouseEntity.findByIdAndUpdate(updateWarehouseCmd.`$id`) {
            it.label = updateWarehouseCmd.label
            it.location = Point(updateWarehouseCmd.latitude, updateWarehouseCmd.longitude)
        })!!
    }

    @OptIn(ExperimentalTime::class)
    override fun removeWarehouse(id: Int): WarehouseDao? = transaction {
        WarehouseDao.from(WarehouseEntity.findSingleByAndUpdate(WarehouseTable.id eq id and WarehouseTable.deletedAt.isNull()) {
            it.deletedAt = Clock.System.now()
        })
    }
}