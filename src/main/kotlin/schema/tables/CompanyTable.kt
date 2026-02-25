package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.BaseTable
import at.eventful.messless.schema.utils.point

object CompanyTable: BaseTable("companies") {

    val label = varchar("label", 255)
    val location = point("location")
}