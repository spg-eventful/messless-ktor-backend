package at.eventful.messless.schema.dto

import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.schema.utils.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: UserRole,
) {
    companion object {
        fun from(user: UserDao): UserDto = UserDto(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            fullName = "${user.firstName} ${user.lastName}",
            email = user.email,
            phone = user.phone,
            role = user.role,
        )
    }
}