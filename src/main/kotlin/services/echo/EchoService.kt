package at.eventful.messless.services.echo

import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.http.*

class EchoService : WebSocketService("echo") {
    override fun ServiceMethod.create(): WebSocketResponse<Any> {
        val cmd = incoming.receiveBody<CreateEchoCmd>()
        return WebSocketResponse(HttpStatusCode.Created, cmd.message)
    }

    override fun ServiceMethod.find(): WebSocketResponse<Any> {
        return WebSocketResponse(
            HttpStatusCode.OK,
            incoming.body,
        )
    }
}