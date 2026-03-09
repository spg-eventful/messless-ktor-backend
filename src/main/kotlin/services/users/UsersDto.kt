package at.eventful.messless.services.users

import at.eventful.messless.repositories.users.DBUser
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val email: String,
    val phone: String,
) {
    companion object {
        fun from(user: DBUser): UserDto = UserDto(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            fullName = "${user.firstName} ${user.lastName}",
            email = user.email,
            phone = user.phone,
        )
    }
}