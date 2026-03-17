package repositories.users

import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.dao.UserDao
import repositories.users.commands.CreateUserCmd

interface UserRepository {
    fun addUser(user: CreateUserCmd): UserDao
    fun allUsers(): List<UserDao>
    fun userById(id: Int): UserDao?
    fun userByEmail(email: String): UserDao?
    fun updateUser(id: Int, user: UpdateUserCmd): UserDao?
    fun removeUser(id: Int): UserDao?
}