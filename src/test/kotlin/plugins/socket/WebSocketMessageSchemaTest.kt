package plugins.socket

import at.eventful.messless.module
import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import testutils.receiveText
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * This test checks the behaviour of the WebSocket message handling implementation.
 */
class WebSocketMessageSchemaTest {
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
            install(WebSockets.Plugin)
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
            install(WebSockets.Plugin)
        }

        client.webSocket("/ws") {
            // Nothing passed
            run {
                send(Frame.Text(""))
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(-1, res.id)
                assertEquals(HttpStatusCode.BadRequest.value, res.statusCode)
            }

            // No id passed
            run {
                send(Frame.Text(";;;"))
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(-1, res.id)
                assertEquals(HttpStatusCode.BadRequest.value, res.statusCode)
            }

            // Method not known
            run {
                send(Frame.Text("0;;;"))
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(0, res.id)
                assertEquals(HttpStatusCode.BadRequest.value, res.statusCode)
            }

            // Service not found test
            run {
                send(Frame.Text("0;read;"))
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(0, res.id)
                assertEquals(HttpStatusCode.BadRequest.value, res.statusCode)
            }

            // Service not found test
            run {
                send(Frame.Text("0;read;service_does_not_exist"))
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(0, res.id)
                assertEquals(HttpStatusCode.NotFound.value, res.statusCode)
            }
        }
    }
}