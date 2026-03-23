package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.Status
import at.eventful.messless.schema.utils.BaseTable
import at.eventful.messless.schema.utils.point
import kotlin.reflect.KClass

object EquipmentTable : BaseTable("equipment") {

    val label = varchar("label", 255)
    val location = point("location", srid = 4326)
    val belongsTo = reference("warehouse_id", WarehouseTable)
    val storage = reference("storage_id", EquipmentStorageTable).nullable()
}