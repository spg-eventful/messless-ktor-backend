package repositories.users

import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.entities.UserEntity
import repositories.users.commands.CreateUserCmd

interface IUsersRepository {
    fun addUser(user: CreateUserCmd): UserEntity
    fun allUsers(): List<UserEntity>
    fun userById(id: Int): UserEntity?
    fun updateUser(id: Int, user: UpdateUserCmd): UserEntity?
    fun removeUser(id: Int): UserEntity?
}