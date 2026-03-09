package plugins.socket

import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.junit.jupiter.api.assertThrows
import testutils.configuredTestApplication
import testutils.receiveText
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * This test checks the behaviour of the WebSocket message handling implementation.
 */
class WebSocketConfigurationTest {
    @Test
    fun testWrongFrameType() = configuredTestApplication {
        client.webSocket("/ws") {
            run {
                send(Frame.Binary(true, ByteArray(8)))
                val res = WebSocketResponse.fromString(receiveText())
                assertEquals(-1, res.id)
                assertEquals(HttpStatusCode.MethodNotAllowed.value, res.statusCode)
            }

            run {
                send(Frame.Close())
                assertThrows<ClosedReceiveChannelException> { receiveText() }
            }
        }
    }
}