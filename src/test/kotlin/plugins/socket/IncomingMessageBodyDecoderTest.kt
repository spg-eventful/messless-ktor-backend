package plugins.socket

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.services.echo.CreateEchoCmd
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import testutils.configuredTestApplication
import testutils.receiveText
import kotlin.test.Test
import kotlin.test.assertEquals

class IncomingMessageBodyDecoderTest {
    private val service = "echo"

    @Test
    fun testNullBody() = configuredTestApplication {
        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, service, Method.CREATE, null
                        ).toString()
                    )
                )
                val res1 = WebSocketResponse.fromString(receiveText())
                assertEquals(400, res1.statusCode)
            }
        }
    }

    @Test
    fun testWrongBody() = configuredTestApplication {
        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, service, Method.CREATE, "{ \"wrong\": true }"
                        ).toString()
                    )
                )
                val res2 = WebSocketResponse.fromString(receiveText())
                assertEquals(400, res2.statusCode)
            }
        }
    }


    @Test
    fun testCorrectBody() = configuredTestApplication {
        client.webSocket("/ws") {
            run {
                send(
                    Frame.Text(
                        IncomingMessage(
                            0, service, Method.CREATE, Json.encodeToString(CreateEchoCmd("test"))
                        ).toString()
                    )
                )
                val res3 = WebSocketResponse.fromString(receiveText())
                assertEquals(201, res3.statusCode)
            }
        }
    }
}