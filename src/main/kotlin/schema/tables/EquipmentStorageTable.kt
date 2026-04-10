package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.BaseTable

object EquipmentStorageTable : BaseTable("equipment_storages") {
    val loggable = reference("loggable", LoggableTable)
}