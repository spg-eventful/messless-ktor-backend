package repositories.users

import at.eventful.messless.repositories.users.DBUser
import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import repositories.users.commands.CreateUserCmd

interface UsersRepository {
    fun addUser(user: CreateUserCmd): DBUser
    fun allUsers(): List<DBUser>
    fun userById(id: Int): DBUser?
    fun updateUser(id: Int, user: UpdateUserCmd): DBUser?
    fun removeUser(id: Int): DBUser?
}