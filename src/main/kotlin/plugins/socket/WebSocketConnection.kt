package at.eventful.messless.plugins.socket

import io.ktor.server.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class WebSocketConnection(val session: DefaultWebSocketServerSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }

    val id: Int = lastId.incrementAndGet()
}