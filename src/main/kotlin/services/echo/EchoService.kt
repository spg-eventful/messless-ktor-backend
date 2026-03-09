package at.eventful.messless.services.echo

import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.http.*
import org.jetbrains.exposed.v1.core.exposedLogger

class EchoService : WebSocketService("echo") {
    override fun ServiceMethod.create(): WebSocketResponse<Any> {
        val cmd = incoming.receiveBody<CreateEchoCmd>()
        if (cmd.checkAuthentication) {
            exposedLogger.info("EchoService consumer called with checkAuthentication! ${connection.auth}")
            if (connection.auth.isAuthenticated) return WebSocketResponse(HttpStatusCode.OK)
            return WebSocketResponse(HttpStatusCode.Forbidden)
        }

        return WebSocketResponse(HttpStatusCode.Created, cmd.message)
    }

    override fun ServiceMethod.find(): WebSocketResponse<Any> {
        return WebSocketResponse(
            HttpStatusCode.OK,
            incoming.body,
        )
    }
}