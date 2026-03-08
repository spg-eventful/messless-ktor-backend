package repositories.users

import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.entities.UserEntity
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import repositories.users.commands.CreateUserCmd
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UsersRepository : IUsersRepository {
    override fun allUsers(): List<UserEntity> {
        return UserEntity.all().toList()
    }

    override fun userById(id: Int): UserEntity? {
        return UserEntity.findById(id)
    }

    override fun addUser(user: CreateUserCmd): UserEntity = transaction {
        UserEntity.new {
            email = user.email
            password = user.plainPassword // TODO: Hash
            firstName = user.firstName
            lastName = user.lastName
        }
    }

    override fun updateUser(id: Int, user: UpdateUserCmd): UserEntity? = transaction {
        UserEntity.findByIdAndUpdate(id) {
            it.email = user.email
            it.firstName = user.firstName
            it.lastName = user.lastName
            it.phone = user.phone
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun removeUser(id: Int): UserEntity? = transaction {
        UserEntity.findByIdAndUpdate(id) {
            it.deletedAt = Clock.System.now()
        }
    }
}