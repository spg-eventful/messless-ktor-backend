package at.eventful.messless.services.echo

import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketResponse
import at.eventful.messless.plugins.socket.WebSocketService
import io.ktor.http.*

class EchoService : WebSocketService("echo") {
    override fun ServiceMethod.find(): WebSocketResponse {
        return WebSocketResponse(
            incoming.id,
            HttpStatusCode.OK,
            incoming.body,
        )
    }
}