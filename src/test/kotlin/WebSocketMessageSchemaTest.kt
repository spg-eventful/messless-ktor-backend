import at.eventful.messless.module
import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * This test checks the behaviour of the WebSocket message handling implementation.
 */
class WebSocketMessageSchemaTest {
    suspend fun DefaultClientWebSocketSession.receiveText() =
        (incoming.receive() as? Frame.Text)?.readText() ?: ""

    // Message schema
    // ID;METHOD;SERVICE;BODY
    // Response schema
    // ID;STATUS;BODY


    @Test
    fun testValidRequest() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(WebSockets)
        }

        client.webSocket("/ws") {
            // 200 OK
            run {
                send(Frame.Text(IncomingMessage(0, "echo", Method.READ, "ECHO").toString()))
                val res = receiveText()
                assertEquals("0;200;ECHO", res)
            }
        }
    }

    @Test
    fun testInvalidRequest() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(WebSockets)
        }

        client.webSocket("/ws") {
            // Nothing passed
            run {
                send(Frame.Text(""))
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(res.id, -1)
                assertEquals(res.statusCode, HttpStatusCode.BadRequest.value)
            }

            // No id passed
            run {
                send(Frame.Text(";;;"))
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(res.id, -1)
                assertEquals(res.statusCode, HttpStatusCode.BadRequest.value)
            }

            // Method not known
            run {
                send(Frame.Text("0;;;"))
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(res.id, 0)
                assertEquals(res.statusCode, HttpStatusCode.BadRequest.value)
            }

            // Service not found test
            run {
                send(Frame.Text("0;read;service_does_not_exist"))
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(res.id, 0)
                assertEquals(res.statusCode, HttpStatusCode.NotFound.value)
            }
        }
    }
}