package at.eventful.messless.repositories.warehouse

import at.eventful.messless.repositories.warehouse.command.CreateWarehouseCmd
import at.eventful.messless.repositories.warehouse.command.UpdateWarehouseCmd
import at.eventful.messless.schema.dao.WarehouseDao

interface WarehouseRepository {
    fun addWarehouse(createWarehouseCommand: CreateWarehouseCmd): WarehouseDao
    fun allWarehouses(): List<WarehouseDao>
    fun warehouseById(id: Int): WarehouseDao?
    fun updateWarehouse(id: Int, updateWarehouseCmd: UpdateWarehouseCmd): WarehouseDao?
    fun removeWarehouse(id: Int): WarehouseDao?
}