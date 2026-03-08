package at.eventful.messless.services.users

import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.entities.UserEntity
import io.ktor.http.*
import repositories.users.UsersRepository
import repositories.users.commands.CreateUserCmd

class UsersService(private val usersRepo: UsersRepository) : WebSocketService("users") {
    override fun ServiceMethod.create(): WebSocketResponse<UserEntity> {
        val cmd = incoming.receiveBody<CreateUserCmd>()
        return WebSocketResponse.from(
            HttpStatusCode.Created,
            usersRepo.addUser(cmd),
        )
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<UserEntity>> {
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            usersRepo.allUsers(),
        )
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<UserEntity> {
        val user = usersRepo.userById(id) ?: throw NotFound("User with id $id not found")
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            user,
        )
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<UserEntity> {
        val updated = usersRepo.updateUser(id, incoming.receiveBody<UpdateUserCmd>())
            ?: throw NotFound("User with id $id not found")
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            updated,
        )
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<Nothing> {
        usersRepo.removeUser(id) ?: throw NotFound("User with id $id not found")
        return WebSocketResponse(
            HttpStatusCode.NoContent
        )
    }
}