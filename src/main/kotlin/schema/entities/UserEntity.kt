package at.eventful.messless.schema.entities

import at.eventful.messless.schema.tables.UserTable
import at.eventful.messless.schema.utils.BaseEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class UserEntity(id: EntityID<Int>) : BaseEntity(id) {
    companion object : IntEntityClass<UserEntity>(UserTable)

    override var createdAt by UserTable.createdAt
    override var updatedAt by UserTable.updatedAt
    override var deletedAt by UserTable.deletedAt

    var email by UserTable.email
    var password by UserTable.password
    var role by UserTable.role
    var phone by UserTable.phone
    var firstName by UserTable.firstName
    var lastName by UserTable.lastName
    var company by CompanyEntity referencedOn UserTable.companyId
}