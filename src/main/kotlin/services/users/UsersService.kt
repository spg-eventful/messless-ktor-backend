package at.eventful.messless.services.users

import at.eventful.messless.errors.responses.BadRequest
import at.eventful.messless.errors.responses.Forbidden
import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.errors.responses.Unauthorized
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.users.UserRepository
import at.eventful.messless.repositories.users.commands.CreateUserCmd
import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.dto.UserDto
import at.eventful.messless.schema.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

class UsersService(app: Application) : WebSocketService("users") {
    val usersRepo: UserRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<UserDto> {
        connection.auth.auth?.let {
            val cmd = incoming.receiveBody<CreateUserCmd>()
            if (it.user.role != UserRole.Admin && cmd.role.asInt() > it.user.role.asInt()) throw Forbidden("You are not allowed to create a user with this role!")
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
        throw Unauthorized()
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<UserDto>> {
        connection.auth.auth?.let {
            val users = if (it.user.role == UserRole.Admin) usersRepo.allUsers() else usersRepo.usersByCompanyId(
                it.user.company?.id ?: throw IllegalStateException()
            )

            return WebSocketResponse.from(
                HttpStatusCode.OK,
                users.map(UserDto::from),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<UserDto> {
        connection.auth.auth?.let {
            val user = usersRepo.userById(id) ?: throw NotFound("User with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                UserDto.from(user),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<UserDto> {
        connection.auth.auth?.let {
            if (it.user.role != UserRole.Admin && it.user.id != id) throw Forbidden("You are only allowed to update your own user!")

            val cmd = incoming.receiveBody<UpdateUserCmd>()
            if (cmd.role.asInt() > it.user.role.asInt()) throw Forbidden("You are not allowed to update to this role!")

            val updated = usersRepo.updateUser(id, cmd) ?: throw NotFound("User with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                UserDto.from(updated),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<Nothing> {
        connection.auth.auth?.let {
            if (it.user.role != UserRole.Admin && it.user.id != id) throw Forbidden("You are only allowed to delete your own user!")

            usersRepo.removeUser(id) ?: throw NotFound("User with id $id not found")
            return WebSocketResponse(
                HttpStatusCode.NoContent
            )
        }
        throw Unauthorized()
    }
}