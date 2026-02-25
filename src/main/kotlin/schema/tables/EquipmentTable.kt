package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.BaseTable
import at.eventful.messless.schema.utils.point

object EquipmentTable: BaseTable("equipments") {

    val label = varchar("label", 255)
    val location = point("location")
    val belongsTo = reference("warehouse_id", WarehouseTable)
    val storage = reference("storage_id", EquipmentStorageTable).nullable()
}