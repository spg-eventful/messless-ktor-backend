package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.BaseTable
import at.eventful.messless.schema.utils.point

object TechnicalLogEntryTable : BaseTable("technical_log_entries") {

    val isCheckIn = bool("is_check_in")
    val attachedTo = reference("equipment_id", EquipmentTable)
    val byUser = reference("user_id", UserTable)
    var loggable = reference("loggable_id", LoggableTable)
    val location = point("location", srid = 4326)
}