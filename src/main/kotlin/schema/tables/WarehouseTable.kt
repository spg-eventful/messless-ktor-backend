package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.LoggableTable

object WarehouseTable : LoggableTable("warehouses") {
    val companyId = reference("company_id", CompanyTable)
}