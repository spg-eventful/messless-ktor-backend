package at.eventful.messless.repositories.users.commands

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserCmd(
    var email: String,
    var plainPassword: String,
    var role: String,
    var phone: String,
    var firstName: String,
    var lastName: String,
    var companyId: Int?,
)