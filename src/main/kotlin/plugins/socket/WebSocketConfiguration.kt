package at.eventful.messless.plugins.socket

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlin.time.Duration.Companion.seconds

fun Application.configureWebSocket() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/ws") {
            log.info("[WS] Connected new consumer. Registering ...")
            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        send(frame.readText())
                    }
                }
            }.onFailure { exception ->
                log.warn("[WS]: ${exception.localizedMessage}")
            }.also {
                log.info("[WS] Removing a consumer ...")
            }
        }
    }
}