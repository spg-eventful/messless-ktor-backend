package services.users

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.users.commands.UpdateUserCmd
import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.schema.utils.UserRole
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import repositories.users.UserRepository
import repositories.users.commands.CreateUserCmd
import testutils.configuredTestApplication
import testutils.receiveText
import kotlin.test.Test
import kotlin.test.assertEquals


@ExtendWith(MockKExtension::class)
class UsersServiceTest {
    val usersRepository = mockk<UserRepository>()

    fun userFakeCreateCmd(): CreateUserCmd = CreateUserCmd(
        "test@abc.com", "banane", UserRole.CompanyAdmin, "+4300000000", "firstname", "lastname"
    )

    @Test
    fun testUserCreation() = configuredTestApplication {
        dependencies.provide<UserRepository> {
            usersRepository
        }
        every { usersRepository.addUser(any()) } returns UserDao.fake(1)

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "users", Method.CREATE, Json.encodeToString(
                                userFakeCreateCmd()
                            )
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(201, res.statusCode)
            }
        }
    }

    @Test
    fun testUserReadNotFound() = configuredTestApplication {
        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "users", Method.READ, "1"
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(404, res.statusCode)
            }
        }
    }

    @Test
    fun testUserRead() = configuredTestApplication {
        dependencies.provide<UserRepository> {
            usersRepository
        }
        every { usersRepository.userById(1) } returns UserDao.fake(1)

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "users", Method.READ, "1"
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(200, res.statusCode)
            }
        }
    }

    @Test
    fun testUserReadAll() = configuredTestApplication {
        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "users", Method.READ
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(200, res.statusCode)
            }
        }
    }

    @Test
    fun testUserUpdate() = configuredTestApplication {
        val fakeUser = UserDao.fake(1)
        val cmd = UpdateUserCmd(
            fakeUser.id,
            fakeUser.email,
            fakeUser.phone,
            fakeUser.firstName,
            fakeUser.lastName,
            fakeUser.role,
        )

        dependencies.provide<UserRepository> {
            usersRepository
        }
        every { usersRepository.updateUser(1, any()) } returns fakeUser

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "users", Method.UPDATE, Json.encodeToString(cmd)
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(200, res.statusCode)
            }
        }
    }

    @Test
    fun testUserUpdateNotFound() = configuredTestApplication {
        val fakeUser = UserDao.fake(1)
        val cmd = UpdateUserCmd(
            2,
            fakeUser.email,
            fakeUser.phone,
            fakeUser.firstName,
            fakeUser.lastName,
            fakeUser.role,
        )

        dependencies.provide<UserRepository> {
            usersRepository
        }
        every { usersRepository.updateUser(2, any()) } returns null

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "users", Method.UPDATE, Json.encodeToString(cmd)
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(404, res.statusCode)
            }
        }
    }

    @Test
    fun testUserDelete() = configuredTestApplication {
        val fakeUser = UserDao.fake(1)
        dependencies.provide<UserRepository> {
            usersRepository
        }
        every { usersRepository.removeUser(1) } returns fakeUser

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "users", Method.DELETE, "1"
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(204, res.statusCode)
            }
        }
    }

}