package at.eventful.messless.repositories.warehouse

import at.eventful.messless.schema.entities.WarehouseEntity
import at.eventful.messless.schema.tables.WarehouseTable
import at.eventful.messless.services.warehouse.Warehouse
import net.postgis.jdbc.geometry.Point
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import repositories.warehouse.command.CreateWarehouseCmd
import repositories.warehouse.command.UpdateWarehouseCmd
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class WarehouseRepository : IWarehouseRepository {
    override fun addWarehouse(createWarehouseCommand: CreateWarehouseCmd): Warehouse {
        val newLocation = Point(createWarehouseCommand.locationX, createWarehouseCommand.locationY)
        val newWarehouseEntity = WarehouseEntity.new {
            label = createWarehouseCommand.label
            location = newLocation
        }.id.value
        return warehouseById(newWarehouseEntity)!!
    }

    @OptIn(ExperimentalTime::class)
    override fun warehouseById(id: Int): Warehouse? {
        val warehouse =
            WarehouseEntity.find { WarehouseTable.id eq id and WarehouseTable.deletedAt.isNull() }.firstOrNull()

        if (warehouse == null) return null

        val locationX = warehouse.location.x
        val locationY = warehouse.location.y

        return Warehouse(
            warehouse.id.value,
            warehouse.label,
            locationX,
            locationY,
            warehouse.company.label
        )
    }

    @OptIn(ExperimentalTime::class)
    override fun allWarehouses(): List<Warehouse> {
        return WarehouseEntity.find { WarehouseTable.deletedAt.isNull() }.toList().stream().map { warehouseEntity ->
            Warehouse(
                warehouseEntity.id.value,
                warehouseEntity.label,
                warehouseEntity.location.x,
                warehouseEntity.location.y,
                warehouseEntity.company.label
            )
        }.toList()
    }

    @OptIn(ExperimentalTime::class)
    override fun updateWarehouse(updateWarehouseCmd: UpdateWarehouseCmd): Warehouse {
        WarehouseEntity.findSingleByAndUpdate(WarehouseTable.id eq updateWarehouseCmd.id and WarehouseTable.deletedAt.isNull()) {
            it.label = updateWarehouseCmd.label
            it.location = Point(updateWarehouseCmd.locationX, updateWarehouseCmd.locationY)
        }
        return warehouseById(updateWarehouseCmd.id)!!
    }

    @OptIn(ExperimentalTime::class)
    override fun removeWarehouse(id: Int) {
        WarehouseEntity.findSingleByAndUpdate(WarehouseTable.id eq id and WarehouseTable.deletedAt.isNull()) {
            it.deletedAt = Clock.System.now()
        }
    }
}