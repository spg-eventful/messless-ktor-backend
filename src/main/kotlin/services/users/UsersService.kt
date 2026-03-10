package at.eventful.messless.services.users

import at.eventful.messless.errors.responses.BadRequest
import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.dto.UserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import repositories.users.UserRepository
import repositories.users.commands.CreateUserCmd

class UsersService(app: Application) : WebSocketService("users") {
    val usersRepo: UserRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<UserDto> {
        val cmd = incoming.receiveBody<CreateUserCmd>()

        try {
            return WebSocketResponse.from(
                HttpStatusCode.Created,
                UserDto.from(usersRepo.addUser(cmd)),
            )
        } catch (e: Exception) {
            if (e.message?.contains("PUBLIC.USERS_EMAIL_UNIQUE") ?: false) {
                throw BadRequest("A user with this email already exists.")
            }
            throw e
        }
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<UserDto>> {
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            usersRepo.allUsers().map(UserDto::from),
        )
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<UserDto> {
        val user = usersRepo.userById(id) ?: throw NotFound("User with id $id not found")
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            UserDto.from(user),
        )
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<UserDto> {
        val updated = usersRepo.updateUser(id, incoming.receiveBody<UpdateUserCmd>())
            ?: throw NotFound("User with id $id not found")
        return WebSocketResponse.from(
            HttpStatusCode.OK,
            UserDto.from(updated),
        )
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<Nothing> {
        usersRepo.removeUser(id) ?: throw NotFound("User with id $id not found")
        return WebSocketResponse(
            HttpStatusCode.NoContent
        )
    }
}