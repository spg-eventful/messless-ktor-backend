package at.eventful.messless.plugins.socket

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketConnection
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.http.*

data class ServiceMethod(val incoming: IncomingMessage, val connection: WebSocketConnection)

open class WebSocketService(val name: String) {
    private fun methodNotAllowed(): WebSocketResponse {
        return WebSocketResponse(
            HttpStatusCode.MethodNotAllowed,
            "Method not implemented/allowed!"
        )
    }

    fun route(
        incoming: IncomingMessage,
        connection: WebSocketConnection
    ): WebSocketResponse {
        val serviceMethod = ServiceMethod(incoming, connection)

        return when (incoming.method) {
            Method.CREATE -> serviceMethod.create()
            Method.READ -> {
                val id = incoming.body?.toIntOrNull() ?: return serviceMethod.find()
                serviceMethod.get(id)
            }

            Method.UPDATE -> serviceMethod.update()
            Method.DELETE -> serviceMethod.delete()
        }
    }

    open fun ServiceMethod.create(): WebSocketResponse {
        return methodNotAllowed()
    }

    open fun ServiceMethod.find(): WebSocketResponse {
        return methodNotAllowed()
    }

    open fun ServiceMethod.get(id: Int): WebSocketResponse {
        return methodNotAllowed()
    }

    open fun ServiceMethod.update(): WebSocketResponse {
        return methodNotAllowed()
    }

    open fun ServiceMethod.delete(): WebSocketResponse {
        return methodNotAllowed()
    }
}