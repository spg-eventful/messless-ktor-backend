package at.eventful.messless.schema.utils

abstract class LoggableTable(name: String): BaseTable(name) {

    val label = varchar("label", 255)
    val location = point("location")

}