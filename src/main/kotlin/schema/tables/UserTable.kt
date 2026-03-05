package at.eventful.messless.schema.tables

import at.eventful.messless.schema.utils.BaseTable
import at.eventful.messless.schema.utils.UserRole

object UserTable : BaseTable("users") {

    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val role = enumerationByName<UserRole>("role", 20)
    val phone = varchar("phone", 20)
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)

    val companyId = reference("company_id", CompanyTable)
}