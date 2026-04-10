package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.BaseTable

object WarehouseTable : BaseTable("warehouses") {
    val companyId = reference("company_id", CompanyTable)
    val loggable = reference("loggable", LoggableTable)
}