package at.eventful.messless.plugins.socket

import at.eventful.messless.plugins.socket.model.WebSocketConnection
import at.eventful.messless.router
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.logging.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import java.util.*
import kotlin.time.Duration.Companion.seconds

internal val WS_LOGGER = KtorSimpleLogger("WebSocket")

fun Application.configureWebSocket() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    WS_LOGGER.info("Installed WebSocket plugin")

    routing {
        val messageConverter = MessageConverter()
        val connections = Collections.synchronizedMap<Int, WebSocketConnection>(LinkedHashMap())

        webSocket("/ws") {
            WS_LOGGER.info("Connected new consumer. Registering ...")

            val connection = WebSocketConnection(this)
            connections[connection.id] = connection

            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        runCatching {
                            val incoming = messageConverter.deserialize(frame.readText())
                            WS_LOGGER.trace("Received message: {}", incoming)
                            send(router.route(incoming, connection).toFrame(incoming.id))
                        }.onFailure {
                            WS_LOGGER.warn(
                                "An error occurred handling the last message: {}",
                                it.localizedMessage
                            )
                        }
                    }
                }
            }.onFailure { exception ->
                WS_LOGGER.warn("[WS]: ${exception.localizedMessage}")
            }.also {
                WS_LOGGER.info("[WS] Removing a consumer ...")
                connections.remove(connection.id)
            }
        }
    }
}