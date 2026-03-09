package at.eventful.messless.repositories.users

import at.eventful.messless.repositories.IConvertibleDBType
import at.eventful.messless.repositories.companies.DBCompany
import at.eventful.messless.schema.entities.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class DBUser(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val company: DBCompany?,
    val password: String,
) {
    companion object : IConvertibleDBType<UserEntity, DBUser> {
        override fun from(entity: UserEntity?): DBUser? = entity?.let {
            DBUser(
                id = entity.id.value,
                firstName = entity.firstName,
                lastName = entity.lastName,
                email = entity.email,
                phone = entity.phone,
                company = if (entity.company != null) DBCompany.from(entity.company!!) else null,
                password = entity.password,
            )
        }

        fun fake(id: Int, email: String = "fake@email.com") = DBUser(
            id = id,
            firstName = "John",
            lastName = "Doe",
            email = email,
            phone = "+43 123 456 7890",
            company = null, // TODO
            password = "password",
        )
    }
}