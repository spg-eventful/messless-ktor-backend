package testutils

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlin.test.assertEquals

suspend fun DefaultClientWebSocketSession.receiveText() =
    (incoming.receive() as? Frame.Text)?.readText() ?: ""

suspend fun DefaultClientWebSocketSession.sendAndAssert(
    service: String,
    method: Method,
    payload: String?,
    expectedStatus: Int
) {
    send(Frame.Text(IncomingMessage(0, service, method, payload).toString()))
    val res = WebSocketResponse.fromString(receiveText())
    assertEquals(expectedStatus, res.statusCode, "Service $service ${method.name} failed with payload $payload")
}