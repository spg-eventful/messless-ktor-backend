package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.BaseTable
import at.eventful.messless.schema.utils.LoggableType
import at.eventful.messless.schema.utils.point

object LoggableTable : BaseTable("loggable") {
    val label = varchar("label", 255)
    val location = point("location", srid = 4326)
    val loggable_type = enumerationByName<LoggableType>("loggable_type", 20)
    val companyId = reference("company_id", CompanyTable)
}