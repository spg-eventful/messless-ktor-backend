package at.eventful.messless.repositories.users.commands

import at.eventful.messless.schema.utils.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserCmd(
    val `$id`: Int,
    var email: String,
    var phone: String,
    var firstName: String,
    var lastName: String,
    var role: UserRole
)