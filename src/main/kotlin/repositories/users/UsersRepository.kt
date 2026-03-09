package repositories.users

import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.entities.UserEntity
import at.eventful.messless.schema.tables.UserTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import repositories.users.commands.CreateUserCmd
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UsersRepository : IUsersRepository {
    @OptIn(ExperimentalTime::class)
    override fun allUsers(): List<UserEntity> = transaction {
        UserEntity.find { (UserTable.deletedAt eq null) }.toList()
    }

    @OptIn(ExperimentalTime::class)
    override fun userById(id: Int): UserEntity? = transaction {
        val user = UserEntity.findById(id)
        return@transaction if (user?.deletedAt == null) user else null
    }

    override fun addUser(user: CreateUserCmd): UserEntity = transaction {
        UserEntity.new {
            email = user.email
            password = user.plainPassword // TODO: Hash
            firstName = user.firstName
            lastName = user.lastName
            phone = user.phone
            role = user.role
        }
    }

    override fun updateUser(id: Int, user: UpdateUserCmd): UserEntity? = transaction {
        UserEntity.findByIdAndUpdate(id) {
            it.email = user.email
            it.firstName = user.firstName
            it.lastName = user.lastName
            it.phone = user.phone
            it.role = user.role
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun removeUser(id: Int): UserEntity? = transaction {
        UserEntity.findByIdAndUpdate(id) {
            it.deletedAt = Clock.System.now()
        }
    }
}