package at.eventful.messless.plugins.socket.model

import at.eventful.messless.plugins.socket.auth.AuthenticationData
import io.ktor.server.websocket.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * WebSocket connections are stored with an id and the WebSocket session to identify them.
 */
class WebSocketConnection(
    val session: DefaultWebSocketServerSession,
    val auth: AuthenticationData = AuthenticationData()
) {
    companion object {
        val lastId = AtomicInteger(0)
    }

    /** A unique id of this connection, atomic, and increments automatically */
    val id: Int = lastId.incrementAndGet()
}