package at.eventful.messless.plugins.socket

import at.eventful.messless.plugins.socket.model.WebSocketConnection
import at.eventful.messless.router
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import java.util.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureWebSocket() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val messageConverter = MessageConverter()
        val connections = Collections.synchronizedMap<Int, WebSocketConnection>(LinkedHashMap())

        webSocket("/ws") {
            log.info("[WS] Connected new consumer. Registering ...")

            val connection = WebSocketConnection(this)
            connections[connection.id] = connection

            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        runCatching {
                            val incoming = messageConverter.deserialize(frame.readText())
                            log.info("[WS] Received message: {}", incoming)
                            send(router.route(incoming, connection).toFrame())
                        }.onFailure {
                            log.warn(
                                "[WS] An error occurred handling the last message: {}",
                                it.localizedMessage
                            )
                        }
                    }
                }
            }.onFailure { exception ->
                log.warn("[WS]: ${exception.localizedMessage}")
            }.also {
                log.info("[WS] Removing a consumer ...")
                connections.remove(connection.id)
            }
        }
    }
}