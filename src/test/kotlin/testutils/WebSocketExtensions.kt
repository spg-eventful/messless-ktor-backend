package testutils

import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*

suspend fun DefaultClientWebSocketSession.receiveText() =
    (incoming.receive() as? Frame.Text)?.readText() ?: ""