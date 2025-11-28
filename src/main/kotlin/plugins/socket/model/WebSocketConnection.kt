package at.eventful.messless.plugins.socket.model

import io.ktor.server.websocket.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * WebSocket connections are stored with an id and the WebSocket session to identify them.
 */
class WebSocketConnection(val session: DefaultWebSocketServerSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }

    /** A unique id of this connection, atomic, and increments automatically */
    val id: Int = lastId.incrementAndGet()
}