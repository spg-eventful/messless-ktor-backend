package at.eventful.messless.plugins.socket

import io.ktor.http.*

open class WebSocketService {
    private fun methodNotAllowed(messageId: Int): WebSocketResponse {
        return WebSocketResponse(
            messageId,
            HttpStatusCode.MethodNotAllowed,
            "Method not implemented/allowed!"
        )
    }

    open fun route(
        method: Method,
        decoded: IncomingMessage,
        connection: WebSocketConnection
    ): WebSocketResponse {
        return when (method) {
            Method.CREATE -> create(decoded, connection)
            Method.READ -> read(decoded, connection)
            Method.UPDATE -> update(decoded, connection)
            Method.DELETE -> delete(decoded, connection)
        }
    }

    // open fun read(incoming: IncomingMessage, connection: WebSocketConnection): WebSocketResponse {
    //     return methodNotAllowed(incoming.id)
    // }

    open fun read(incoming: IncomingMessage, connection: WebSocketConnection): WebSocketResponse {
        return methodNotAllowed(incoming.id)
    }

    open fun create(incoming: IncomingMessage, connection: WebSocketConnection): WebSocketResponse {
        return methodNotAllowed(incoming.id)
    }

    open fun update(incoming: IncomingMessage, connection: WebSocketConnection): WebSocketResponse {
        return methodNotAllowed(incoming.id)
    }

    open fun delete(incoming: IncomingMessage, connection: WebSocketConnection): WebSocketResponse {
        return methodNotAllowed(incoming.id)
    }
}