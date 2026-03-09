package repositories.users

import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.schema.entities.UserEntity
import at.eventful.messless.schema.tables.UserTable
import at.eventful.messless.services.auth.hashWithDefaultConfig
import de.mkammerer.argon2.Argon2
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import repositories.users.commands.CreateUserCmd
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class UserRepositoryImpl(val argon2: Argon2) : UserRepository {
    @OptIn(ExperimentalTime::class)
    override fun allUsers(): List<UserDao> = transaction {
        UserEntity.find { (UserTable.deletedAt neq null) }.toList().map(UserDao::from) as List<UserDao>
    }

    @OptIn(ExperimentalTime::class)
    override fun userById(id: Int): UserDao? = transaction {
        val user = UserEntity.findById(id)
        return@transaction if (user?.deletedAt == null) UserDao.from(user) else null
    }

    @OptIn(ExperimentalTime::class)
    override fun userByEmail(email: String): UserDao? = transaction {
        val user = UserEntity.find { (UserTable.email eq email) and (UserTable.deletedAt neq null) }.firstOrNull()
        return@transaction UserDao.from(user)
    }

    override fun addUser(user: CreateUserCmd): UserDao = transaction {
        UserDao.from(UserEntity.new {
            email = user.email
            password = argon2.hashWithDefaultConfig(user.plainPassword)
            firstName = user.firstName
            lastName = user.lastName
            phone = user.phone
            role = user.role
        })!!
    }

    override fun updateUser(id: Int, user: UpdateUserCmd): UserDao? = transaction {
        UserDao.from(UserEntity.findByIdAndUpdate(id) {
            it.email = user.email
            it.firstName = user.firstName
            it.lastName = user.lastName
            it.phone = user.phone
            it.role = user.role
        })
    }

    @OptIn(ExperimentalTime::class)
    override fun removeUser(id: Int): UserDao? = transaction {
        UserDao.from(UserEntity.findByIdAndUpdate(id) {
            it.deletedAt = Clock.System.now()
        })
    }
}