package at.eventful.messless.repositories.users.commands

import at.eventful.messless.schema.utils.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserCmd(
    var email: String,
    var plainPassword: String,
    var role: UserRole,
    var phone: String,
    var firstName: String,
    var lastName: String,
    var companyId: Int?,
)