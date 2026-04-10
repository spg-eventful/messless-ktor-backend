package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.BaseTable

object EquipmentTable : BaseTable("equipment") {

    val label = varchar("label", 255)
    val belongsTo = reference("warehouse_id", WarehouseTable)
    val isStorage = reference("storage_id", EquipmentStorageTable).nullable()
}