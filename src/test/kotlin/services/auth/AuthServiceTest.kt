package services.auth

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.services.auth.commands.CreateAuthBasicCmd
import at.eventful.messless.services.auth.commands.CreateAuthJWTCmd
import at.eventful.messless.services.echo.CreateEchoCmd
import com.auth0.jwt.JWT
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import repositories.users.UserRepository
import testutils.configuredTestApplication
import testutils.receiveText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
class AuthServiceTest {
    val usersRepository = mockk<UserRepository>()

    @Nested
    inner class BasicAuthTests {
        @Test
        fun testCreateAuthBasicWithCorrectCredentials() = configuredTestApplication {
            val fakeUser = UserDao.fake(99)

            dependencies.provide<UserRepository> {
                usersRepository
            }
            every { usersRepository.userByEmail(any()) } returns fakeUser

            client.webSocket("/ws") {
                run {
                    send(
                        Frame.Text(
                            IncomingMessage(
                                0, "auth", Method.CREATE, Json.encodeToString(
                                    CreateAuthBasicCmd(fakeUser.email, fakeUser.password)
                                )
                            ).toString()
                        )
                    )
                    val res = WebSocketResponse.fromString(receiveText())
                    assertEquals(201, res.statusCode)

                    val jwt = JWT.decode(res.body)
                    val sub = jwt.claims["id"]?.asInt()
                    assertNotNull(sub)
                    assertEquals(fakeUser.id, sub, "subject does not match!")

                    checkAuth()
                }
            }
        }

        @Test
        fun testCreateAuthBasicWithWrongCredentials() = configuredTestApplication {
            val fakeUser = UserDao.fake(99)

            dependencies.provide<UserRepository> {
                usersRepository
            }
            every { usersRepository.userByEmail(any()) } returns fakeUser

            client.webSocket("/ws") {
                run {
                    send(
                        Frame.Text(
                            IncomingMessage(
                                0, "auth", Method.CREATE, Json.encodeToString(
                                    CreateAuthBasicCmd(fakeUser.email, "This is wrong?")
                                )
                            ).toString()
                        )
                    )
                    val res = WebSocketResponse.fromString(receiveText())
                    assertEquals(401, res.statusCode)

                    checkAuth(false)
                }
            }
        }

        @Test
        fun testCreateAuthBasicWithNonexistentUser() = configuredTestApplication {
            dependencies.provide<UserRepository> {
                usersRepository
            }
            every { usersRepository.userByEmail(any()) } returns null

            client.webSocket("/ws") {
                run {
                    send(
                        Frame.Text(
                            IncomingMessage(
                                0, "auth", Method.CREATE, Json.encodeToString(
                                    CreateAuthBasicCmd("does@not.exist", "This is wrong?")
                                )
                            ).toString()
                        )
                    )
                    val res = WebSocketResponse.fromString(receiveText())
                    assertEquals(401, res.statusCode)

                    checkAuth(false)
                }
            }
        }
    }

    @Nested
    inner class JWTAuthTests {
        @Test
        fun testAuthenticateUsingValidJWT() = configuredTestApplication {
            val fakeUser = UserDao.fake(99)

            dependencies.provide<UserRepository> {
                usersRepository
            }
            every { usersRepository.userById(fakeUser.id) } returns fakeUser
            every { usersRepository.userByEmail(fakeUser.email) } returns fakeUser

            var jwt: String? = null
            client.webSocket("/ws") {
                run {
                    // Get a valid JWT for this test
                    send(
                        Frame.Text(
                            IncomingMessage(
                                0, "auth", Method.CREATE, Json.encodeToString(
                                    CreateAuthBasicCmd(fakeUser.email, fakeUser.password)
                                )
                            ).toString()
                        )
                    )
                    val res = WebSocketResponse.fromString(receiveText())
                    assertEquals(201, res.statusCode)
                    jwt = res.body
                }
            }

            client.webSocket("/ws") {
                run {
                    send(
                        Frame.Text(
                            IncomingMessage(
                                0, "auth", Method.CREATE, Json.encodeToString(
                                    CreateAuthJWTCmd(jwt!!)
                                )
                            ).toString()
                        )
                    )
                    val res = WebSocketResponse.fromString(receiveText())
                    assertEquals(200, res.statusCode)

                    checkAuth()
                }
            }
        }
    }

    /**
     * Use the echo service to verify auth
     */
    private suspend fun DefaultClientWebSocketSession.checkAuth(shouldBeAuthed: Boolean = true) {
        send(
            Frame.Text(
                IncomingMessage(
                    0, "echo", Method.CREATE, Json.encodeToString(
                        CreateEchoCmd("", true)
                    )
                ).toString()
            )
        )
        val res = WebSocketResponse.fromString(receiveText())
        assertEquals(if (shouldBeAuthed) 200 else 401, res.statusCode)
    }
}