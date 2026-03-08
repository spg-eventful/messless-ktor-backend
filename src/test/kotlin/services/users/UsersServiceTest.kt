package services.users

import at.eventful.messless.module
import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.schema.utils.UserRole
import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import io.mockk.junit5.MockKExtension
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.test.KoinTest
import repositories.users.commands.CreateUserCmd
import testutils.receiveText
import kotlin.test.Test
import kotlin.test.assertEquals


@ExtendWith(MockKExtension::class)
class UsersServiceTest : KoinTest {
    @Test
    fun testUserCreation() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(WebSockets.Plugin)
        }

        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, "users", Method.CREATE, Json.encodeToString(
                                CreateUserCmd(
                                    "test@abc.com", "banane",
                                    UserRole.CompanyAdmin, "+4300000000", "firstname", "lastname"
                                )
                            )
                        ).toString()
                    )
                )
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(201, res.statusCode)
            }
        }
    }
}