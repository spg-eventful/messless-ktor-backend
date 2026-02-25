package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.BaseTable

object TechnicalLogEntryTable: BaseTable("technical_log_entries") {

    val isCheckIn = bool("is_check_in")
    val attachedTo = reference("equipment_id", EquipmentTable)
    val byUser = reference("user_id", UserTable)
    var loggable = integer("loggable")
}