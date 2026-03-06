package at.eventful.messless.repositories.warehouse

import at.eventful.messless.services.warehouse.Warehouse
import repositories.warehouse.command.CreateWarehouseCmd
import repositories.warehouse.command.UpdateWarehouseCmd

interface IWarehouseRepository {
    fun addWarehouse(createWarehouseCommand: CreateWarehouseCmd): Warehouse
    fun allWarehouses(): List<Warehouse>
    fun warehouseById(id: Int): Warehouse?
    fun updateWarehouse(updateWarehouseCmd: UpdateWarehouseCmd): Warehouse
    fun removeWarehouse(id: Int)
}