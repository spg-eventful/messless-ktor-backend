package at.eventful.messless.repositories.users.commands

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserCmd(
    var email: String,
    var phone: String,
    var firstName: String,
    var lastName: String
)