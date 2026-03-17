package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.UserEntity
import at.eventful.messless.schema.utils.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserDao(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val company: CompanyDao?,
    val password: String,
    val role: UserRole,
) {
    companion object : ConvertibleDao<UserEntity, UserDao> {
        override fun from(entity: UserEntity?): UserDao? = entity?.let {
            UserDao(
                id = entity.id.value,
                firstName = entity.firstName,
                lastName = entity.lastName,
                email = entity.email,
                phone = entity.phone,
                company = if (entity.company != null) CompanyDao.from(entity.company!!) else null,
                password = entity.password,
                role = entity.role,
            )
        }

        fun fake(id: Int, email: String = "fake@email.com") = UserDao(
            id = id,
            firstName = "John",
            lastName = "Doe",
            email = email,
            phone = "+43 123 456 7890",
            company = null, // TODO
            password = "password",
            role = UserRole.Worker
        )
    }
}