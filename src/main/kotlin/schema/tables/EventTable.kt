package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.BaseTable

object EventTable : BaseTable("events") {
    val loggable = reference("loggable", LoggableTable)
}