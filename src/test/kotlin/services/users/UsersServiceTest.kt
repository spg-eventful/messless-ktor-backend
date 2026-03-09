package services.users

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.schema.utils.UserRole
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import io.mockk.junit5.MockKExtension
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.test.KoinTest
import repositories.users.commands.CreateUserCmd
import testutils.configuredTestApplication
import testutils.receiveText
import kotlin.test.Test
import kotlin.test.assertEquals


@ExtendWith(MockKExtension::class)
class UsersServiceTest : KoinTest {
    fun userFakeCreateCmd(): CreateUserCmd = CreateUserCmd(
        "test@abc.com", "banane",
        UserRole.CompanyAdmin, "+4300000000", "firstname", "lastname"
    )

    @Test
    fun testUserCreation() = configuredTestApplication {
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
        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "users", Method.CREATE, Json.encodeToString(userFakeCreateCmd())
                        ).toString()
                    )
                )
                val cr = WebSocketResponse.fromString(receiveText())
                assertEquals(201, cr.statusCode)

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
}